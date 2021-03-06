package com.java.essence_36;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by lw on 14-5-20.
 * <p>
 * 运行内存-Xms512m -Xmx512m -XX:+PrintGCDetails
 * 测试的结果与实际两者的区别不符，ArrayList居然任何地方表现优秀
 * ！！！待追究原因...
 * <p>
 * ------------------------------------------------
 * ArrayList  末尾添加数据[50w次]测试：执行耗时：8ms
 * LinkedList 末尾添加数据[50w次]测试：执行耗时：15ms
 * Q:
 * ArrayList如果没有指定大小会动态扩展，涉及到数组的复制
 * ------------------------------------------------
 * <p>
 * ------------------------------------------------
 * ArrayList  随机位置添加数据[1w次]测试：执行耗时：516ms
 * LinkedList 随机位置添加数据[1w次]测试：执行耗时：3104ms
 * Q:
 * ArrayList每次随机插入时候进行一次数组的复制
 * 表现应该没有LinkedList优秀
 * ------------------------------------------------
 * <p>
 * ------------------------------------------------
 * ArrayList  随机位置删除次数[1w次]测试：执行耗时：508ms
 * LinkedList 随机位置删除次数[1w次]测试：执行耗时：3621ms
 * Q：
 * ArrayList 每次删除一个元素时候将进行数组的复制，引用下标-1
 * 因此越靠前开销越大
 * LinkedList删除时候判断位置是{前半段、中间、后半段}去删除
 * ArrayList应该没有LinkedList优秀
 * ------------------------------------------------
 * <p>
 * ------------------------------------------------
 * ArrayList  forEach2循环读取测试：执行耗时：10ms
 * LinkedList forEach2循环读取测试：执行耗时：23ms
 * <p>
 * ArrayList  iterator2循环读取测试：执行耗时：9ms
 * LinkedList iterator2循环读取测试：执行耗时：29ms
 * <p>
 * ArrayList  for循环读取测试：执行耗时：6ms
 * LinkedList for循环读取测试： 无穷大...
 * Q:
 * 随机访问ArrayList优秀
 * ------------------------------------------------
 */
public class ArrayList2LinkedList_Compare {


    private static final int INIT_SIZE = 500000;
    private static final int ADDRANDOM_SIZE = 10000;
    public static ArrayList<Object> arrayList = new ArrayList(INIT_SIZE);
    public static LinkedList<Object> linkedList = new LinkedList();
    private static Object object = new Object();
    private static Random random = new Random();

    public static void main(String[] args) {
        Proxy_Cglib proxy_cglib = new Proxy_Cglib();
        ArrayList2LinkedList_Compare compare =
                (ArrayList2LinkedList_Compare) proxy_cglib.getInstance(new ArrayList2LinkedList_Compare());
        compare.add2Last("ArrayList  末尾添加数据[50w次]测试：", arrayList);
        compare.add2Last("LinkedList 末尾添加数据[50w次]测试：", linkedList);

        System.out.println();
        compare.add2Random("ArrayList  随机位置添加数据[1w次]测试：", arrayList);
        compare.add2Random("LinkedList 随机位置添加数据[1w次]测试：", linkedList);

        System.out.println();
        compare.remove2Random("ArrayList  随机位置删除次数[1w次]测试：", arrayList);
        compare.remove2Random("LinkedList 随机位置删除次数[1w次]测试：", linkedList);

        System.out.println();
        compare.forEach2("ArrayList  forEach2循环读取测试：", arrayList);
        compare.forEach2("LinkedList forEach2循环读取测试：", linkedList);

        System.out.println();
        compare.iterator2("ArrayList  iterator2循环读取测试：", arrayList);
        compare.iterator2("LinkedList iterator2循环读取测试：", linkedList);

        System.out.println();
        compare.for2("ArrayList  for循环读取测试：", arrayList);
        compare.for2("LinkedList for循环读取测试：", linkedList);

    }

    public void add2Last(String state, List list) {
        for (int i = 0; i < INIT_SIZE; i++) {
            list.add(object);
        }
    }

    public void add2Random(String state, List list) {
        int temp = 0;
        for (int i = 0; i < ADDRANDOM_SIZE; i++) {
            temp = random.nextInt(INIT_SIZE);
            list.add(temp, object);
        }
    }

    public void remove2Random(String state, List list) {
        int temp = 0;
        for (int i = 0; i < ADDRANDOM_SIZE; i++) {
            temp = random.nextInt(INIT_SIZE);
            list.remove(temp);
        }
    }

    public void forEach2(String state, List list) {

        for (Object o : list) {
            object = o;
        }
    }

    public void iterator2(String state, List list) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            object = iterator.next();
        }
    }

    public void for2(String state, List list) {
        for (int i = 0; i < INIT_SIZE; i++) {
            object = list.get(i);
        }
    }
}


/**
 * Created by lw on 14-5-1.
 * <p>
 * cglib动态代理
 * 项目中计时方法执行计时使用
 */
class Proxy_Cglib implements MethodInterceptor {

    /**
     * 创建代理对象
     *
     * @param object
     * @return
     */
    public Object getInstance(Object object) {
        Object object1 = object;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(object1.getClass());
        // 回调方法
        enhancer.setCallback(this);
        // 创建代理对象
        return enhancer.create();
    }

    @Override
    // 回调方法
    public Object intercept(Object obj, Method method, Object[] args,
                            MethodProxy proxy) throws Throwable {
        long start = System.currentTimeMillis();
        proxy.invokeSuper(obj, args);
        System.out.println(args[0] + "执行耗时：" + (System.currentTimeMillis() - start) + "ms");
        return null;
    }
}