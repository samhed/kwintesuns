package ida.liu.se.kwintesuns.server;

import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * Created by IntelliJ IDEA.
 * User: aviadbendov
 * Date: Apr 11, 2008
 * Time: 6:05:14 PM
 * To change this template use File | Settings | File Templates.
 */
public final class DependencyInjection {
    public static Module createModule() {
        try {
            return (Module) Class.forName(System.getProperty("guice-module")).newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T createInstnace(Class<T> clz) {
        return Guice.createInjector(DependencyInjection.createModule()).getInstance(clz);
    }
}