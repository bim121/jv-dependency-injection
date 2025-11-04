package mate.academy.lib;

import java.lang.reflect.Field;
import mate.academy.service.FileReaderService;
import mate.academy.service.ProductParser;
import mate.academy.service.ProductService;

public class Injector {
    private static final Injector injector = new Injector();

    public static Injector getInjector() {
        return injector;
    }

    public Object getInstance(Class<?> interfaceClazz) {
        try {
            if (interfaceClazz.isInterface()) {
                interfaceClazz = findImplementation(interfaceClazz);
            }
            if (!interfaceClazz.isAnnotationPresent(Component.class)) {
                throw  new RuntimeException("Class " + interfaceClazz.getName()
                        + " is not annotated with @Component");
            }

            Object instance = interfaceClazz.getDeclaredConstructor().newInstance();
            for (Field field: interfaceClazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Class<?> fieldType = field.getType();
                    Class<?> implClass = findImplementation(fieldType);
                    Object fieldInstance = getInstance(implClass);
                    field.setAccessible(true);
                    field.set(instance, fieldInstance);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of "
                    + interfaceClazz.getName(), e);
        }
    }

    private Class<?> findImplementation(Class<?> interfaceClazz) {
        if (interfaceClazz == ProductParser.class) {
            return mate.academy.service.impl.ProductParserImpl.class;
        } else if (interfaceClazz == FileReaderService.class) {
            return mate.academy.service.impl.FileReaderServiceImpl.class;
        } else if (interfaceClazz == ProductService.class) {
            return mate.academy.service.impl.ProductServiceImpl.class;
        } else {
            throw new RuntimeException("No implementation found for " + interfaceClazz.getName());
        }
    }
}
