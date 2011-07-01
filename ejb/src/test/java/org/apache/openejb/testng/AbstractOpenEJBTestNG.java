package org.apache.openejb.testng;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;

import org.apache.openejb.InjectionProcessor;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.ConfigurationFactory;
import org.apache.openejb.config.ConnectorModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.PersistenceModule;
import org.apache.openejb.core.CoreDeploymentInfo;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.ivm.naming.InitContextFactory;
import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.ManagedBean;
import org.apache.openejb.jee.TransactionType;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.junit.Configuration;
import org.apache.openejb.junit.Module;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.OptionsLog;
import org.apache.xbean.finder.ClassFinder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;


/**
 * @author Romain Manni-Bucau
 */
public abstract class AbstractOpenEJBTestNG {
    private Assembler assembler;
    private ThreadContext previous;
    private AppInfo appInfo;

    @BeforeClass public final void initOpenEJB() throws Exception {
        validate();
        deploy();
    }

    @AfterClass public final void closeOpenEJB() throws Exception {
        if (previous != null) {
            ThreadContext.exit(previous);
        }
        if (assembler != null && appInfo != null) {
            assembler.destroyApplication(appInfo.path);
        }
        SystemInstance.reset();
    }

    private void validate() {
        List<Throwable> errors = new ArrayList<Throwable>();
        Class<?> testClass = getClass();
        ClassFinder finder = new ClassFinder(testClass);

        final List<Method> configs = finder.findAnnotatedMethods(Configuration.class);
        if (configs.size() > 1) {
            final String gripe = "Test class should have no more than one @Configuration method";
            errors.add(new Exception(gripe));
        }

        for (Method method : configs) {
            final Class<?> type = method.getReturnType();
            if (!Properties.class.isAssignableFrom(type)) {
                final String gripe = "@Configuration method must return " + Properties.class.getName();
                errors.add(new Exception(gripe));
            }
        }

        int appModules = 0;
        int modules = 0;
        Class[] moduleTypes = {EjbJar.class, EnterpriseBean.class, Persistence.class, PersistenceUnit.class, Connector.class, Application.class};
        for (Method method : finder.findAnnotatedMethods(Module.class)) {

            modules++;

            final Class<?> type = method.getReturnType();

            if (Application.class.isAssignableFrom(type)) {

                appModules++;

            } else if (!isValidModuleType(type, moduleTypes)) {
                final String gripe = "@Module method must return " + Join.join(" or ", moduleTypes).replaceAll("(class|interface) ", "");
                errors.add(new Exception(gripe));
            }
        }

        if (appModules > 1) {
            final String gripe = "Test class should have no more than one @Module method that returns " + Application.class.getName();
            errors.add(new Exception(gripe));
        }

        if (modules < 1) {
            final String gripe = "Test class should have at least one @Module method";
            errors.add(new Exception(gripe));
        }

        if (!errors.isEmpty()) {
            throw new InitializationError(errors);
        }
    }

    private boolean isValidModuleType(Class<?> type, Class[] moduleTypes) {
        for (Class moduleType : moduleTypes) {
            if (moduleType.isAssignableFrom(type)) return true;
        }
        return false;
    }

    public static class InitializationError extends RuntimeException {
        private List<Throwable> errors;

        public InitializationError(List<Throwable> err) {
            errors = err;
        }

        public List<Throwable> getErrors() {
            return errors;
        }

        @Override public String toString() {
            return "InitializationError { " + errors.toString() + " }";
        }
    }

    private void deploy() throws Exception {
        final Class<?> javaClass = getClass();
        final ClassLoader loader = javaClass.getClassLoader();
        AppModule appModule = new AppModule(loader, javaClass.getSimpleName());

        // Add the test case as an @ManagedBean
        {
            final EjbJar ejbJar = new EjbJar();
            final OpenejbJar openejbJar = new OpenejbJar();
            final ManagedBean bean = ejbJar.addEnterpriseBean(new ManagedBean(javaClass));
            bean.setTransactionType(TransactionType.BEAN);
            final EjbDeployment ejbDeployment = openejbJar.addEjbDeployment(bean);
            ejbDeployment.setDeploymentId(javaClass.getName());

            appModule.getEjbModules().add(new EjbModule(ejbJar, openejbJar));
        }

        Application application = null;

        // Invoke the @Module producer methods to build out the AppModule
        ClassFinder finder = new ClassFinder(javaClass);
        final List<Method> configs = finder.findAnnotatedMethods(Module.class);
        for (Method method : configs) {

            final Object obj = method.invoke(this);

            if (obj instanceof EjbJar) {

                final EjbJar ejbJar = (EjbJar) obj;
                appModule.getEjbModules().add(new EjbModule(ejbJar));

            } else if (obj instanceof EnterpriseBean) {

                final EnterpriseBean bean = (EnterpriseBean) obj;
                final EjbJar ejbJar = new EjbJar();
                ejbJar.addEnterpriseBean(bean);
                appModule.getEjbModules().add(new EjbModule(ejbJar));

            } else if (obj instanceof Application) {

                application = (Application) obj;

            } else if (obj instanceof Connector) {

                final Connector connector = (Connector) obj;
                appModule.getResourceModules().add(new ConnectorModule(connector));

            } else if (obj instanceof Persistence) {

                final Persistence persistence = (Persistence) obj;
                appModule.getPersistenceModules().add(new PersistenceModule("", persistence));

            } else if (obj instanceof PersistenceUnit) {

                final PersistenceUnit unit = (PersistenceUnit) obj;
                appModule.getPersistenceModules().add(new PersistenceModule("", new Persistence(unit)));
            }
        }

        // Application is final in AppModule, which is fine, so we'll create a new one and move everything
//        if (application != null) {
//            final AppModule newModule = new AppModule(appModule.getClassLoader(), appModule.getModuleId(), application, false);
//            newModule.getClientModules().addAll(appModule.getClientModules());
//            newModule.getPersistenceModules().addAll(appModule.getPersistenceModules());
//            newModule.getEjbModules().addAll(appModule.getEjbModules());
//            newModule.getConnectorModules().addAll(appModule.getConnectorModules());
//            appModule = newModule;
//        }

        // For the moment we just take the first @Configuration method
        // maybe later we can add something fancy to allow multiple configurations using a qualifier
        // as a sort of altDD/altConfig concept.  Say for example the altDD prefix might be "foo",
        // we can then imagine something like this:
        // @Foo @Configuration public Properties alternateConfig(){...}
        // @Foo @Module  public Properties alternateModule(){...}
        // anyway, one thing at a time ....

        final Properties configuration = new Properties();
        configuration.put("openejb.deployments.classpath", "false");

        final List<Method> methods = finder.findAnnotatedMethods(Configuration.class);
        for (Method method : methods) {
            final Object o = method.invoke(this);
            if (o instanceof Properties) {
                Properties properties = (Properties) o;
                configuration.putAll(properties);
            }
        }

        if (SystemInstance.isInitialized()) SystemInstance.reset();

        SystemInstance.init(configuration);
        SystemInstance.get().setProperty("openejb.embedded", "true");
        Logger.configure();
        OptionsLog.install();

        ConfigurationFactory config = new ConfigurationFactory();
        config.init(SystemInstance.get().getProperties());

        assembler = new Assembler();
        assembler.buildContainerSystem(config.getOpenEjbConfiguration());

        appInfo = config.configureApplication(appModule);

        assembler.createApplication(appInfo);

        final ContainerSystem containerSystem = SystemInstance.get().getComponent(ContainerSystem.class);
        final CoreDeploymentInfo context = (CoreDeploymentInfo) containerSystem.getDeploymentInfo(javaClass.getName());

        Context jndi = (Context) context.getJndiEnc().lookup("comp/env");
        
        InjectionProcessor processor = new InjectionProcessor(this, context.getInjections(), jndi);

        processor.createInstance();

        System.setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY, InitContextFactory.class.getName());

        previous = ThreadContext.enter(new ThreadContext(context, null, Operation.BUSINESS));
    }
}
