package me.zyee.format;

/**
 * @author yee
 * @date 2018/11/14
 */
public interface CodeFormat {
    String mockMethodStartFormat(String beanName, String methodName);

    String mockMethodEndFormat(String beanName);

    String mockObjectHeadFormat(String className, String beanName);


    String replay();

    String start(String... args);
}
