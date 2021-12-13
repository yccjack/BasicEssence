# 多维度解析Java 动态代理原理

> 作者：[**哈士奇-柏羲**](https://space.bilibili.com/232459430)
>
> 链接： https://www.bilibili.com/read/cv14314757?spm_id_from=333.999.0.0

# 本文将从以下几个维度中详细说明JDK动态代理的原理：

1. 使用维度
2. Java字节码维度
3. Java代理类实现维度

笔者酷爱研究技术底层，良心做事，拒绝广告、韭菜，专注于研究技术细节，有任何问题都可进入QQ群：250431014进行技术交流，如有技术难题继续解决，可以加笔者微信：bx_java，定竭尽所能帮助解决。由于笔者水平有限，仅做分享，如有错误之处，还望读者不吝批评指正~

# 使用实例

我们可以从实例中看到，使用代理类的流程如下：

1. 创建代理类Class对象
2. 创建代理类的实例对象
3. 创建回调处理器InvocationHandler实例
4. 调用实例方法

```clike
public class ProxyDemo {
   public static void main(String[] args) throws Exception {
       System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true"); // 将创建的代理类$Proxy0的字节码保存
       InvocationHandler handler = new MyInvocationHandler(); // 创建回调函数
       Class<?> proxyClass = Proxy.getProxyClass(Foo.class.getClassLoader(), Foo.class); // 创建代理类
       Foo f = (Foo) proxyClass.getConstructor(InvocationHandler.class).newInstance(handler); // 生成代理对象
       f.foo(); // 调用代理对象方法
   }

   // 回调处理器
   private static class MyInvocationHandler implements InvocationHandler {

       @Override
       public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
           System.out.println(method.getName());
           return null;
       }
   }

   private interface Foo {
       void foo();
   }
}
```

逻辑推理

从上面的使用流程中我们可以尝试推理下：为何如此设计？我们想想，在Java中  类是对象的模板，对象是模板的实例，那么这时我们需要创建一个代理的类就毋庸置疑了，同时我们肯定也需要创建一个模板的实例，也即代理对象Foo，我们从实例中可以看到是通过构造函数传入InvocationHandler来构建类的实例，相信熟悉反射的读者应该能看明白。那么从Proxy.getProxyClass(Foo.class.getClassLoader(), Foo.class)中我们看到，传入了类加载器和接口的Class对象，这一切都说得通了：

1. 底层肯定通过某种方式创建了某个类，该类实现了Foo接口
2. 当我们调用该类实现的Foo接口的foo方法时，将会自动的回调我们传入的InvocationHandler方法
3. 由于JVM加载类需要类加载器，这时我们还需要传递一个类加载器：Foo.class.getClassLoader()

# 调用层面描述

接下来我们来看看该类生成的代理类$Proxy0的部分重点源码，从源码中我们得到以下信息：

1. $Proxy0继承自Proxy类，实现了Foo接口
2. 构造器传入InvocationHandler对象
3. 实现的Foo接口的方法修饰为final方法，同时在该方法中直接调用InvocationHandler的invoke方法
4. 在静态方法中，预创建了foo方法所需要的反射对象，这时我们可以考虑为何如此做？读者可以看看InvocationHandler中需要传入该Method实例，而在类初始化时就进行创建可以有效的提高调用性能

```clike
final class $Proxy0 extends Proxy implements Foo {
   // 构造器传入InvocationHandler对象
   public $Proxy0(InvocationHandler var1) throws  {
       super(var1);
   }

   // 实现的Foo接口方法
   public final void foo() throws  {
       try {
           super.h.invoke(this, m3, (Object[])null); // 调用InvocationHandler的invoke方法
       } catch (RuntimeException | Error var2) {
           throw var2;
       } catch (Throwable var3) {
           throw new UndeclaredThrowableException(var3);
       }
   }

   // 预创建反射需要的Method对象
   static {
       try {
           ...
           m3 = Class.forName("org.com.msb.ProxyDemo$Foo").getMethod("foo");
       } catch (NoSuchMethodException var2) {
           throw new NoSuchMethodError(var2.getMessage());
       } catch (ClassNotFoundException var3) {
           throw new NoClassDefFoundError(var3.getMessage());
       }
   }
}
```

# Proxy类创建原理

在上面我们看到了产生的代理类的源码实现，核心操作我们看到是在Proxy类中执行，我们来看看Proxy的实现过程，这里我们主要看getProxyClass方法，流程如下：

1. 克隆一个传入的接口类数组，通过复制该数组，我们可以保证在创建过程中intfs的定性（也即不会被外部方法进行修改）

2. 通过缓存获取，如果没有获取成功，那么通过ProxyClassFactory类创建代理类对象

3. ProxyClassFactory类实现了实际创建代理类的核心方法，创建代理Class对象的过程如下：

4. 1. 遍历接口Class数组，验证类加载器、接口类型、是否实现多个相同接口等等验证操作
2. 定义代理类所属包名和访问修饰符
3. 使用 proxyClassNamePrefix + nextUniqueNumber 生成代理类的名字
4. 通过ProxyGenerator类创建代理类的字节码文件
5. 通过defineClass JNI方法进行字节码加载操作，该操作将会返回Class实例

```clike
public class Proxy implements java.io.Serializable {
   // 用于在实现方法中回调的调用处理器
   protected InvocationHandler h;

   // 保存该处理器对象构造方法
   protected Proxy(InvocationHandler h) {
       Objects.requireNonNull(h);
       this.h = h;
   }

   // 构建代理类的Class对象
   public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces)
       throws IllegalArgumentException
   {
       final Class<?>[] intfs = interfaces.clone(); // 克隆一个传入的接口类数组，通过复制该数组，我们可以保证在创建过程中intfs的定性（也即不会被外部方法进行修改）
       return getProxyClass0(loader, intfs);
   }

   // 执行创建操作
   private static Class<?> getProxyClass0(ClassLoader loader,Class<?>... interfaces) {
       // 限制接口数量为2byte大小（JVM规范定义）
       if (interfaces.length > 65535) {
           throw new IllegalArgumentException("interface limit exceeded");
       }
       // 通过缓存获取，如果没有获取成功，那么创建代理类对象
       return proxyClassCache.get(loader, interfaces);
   }

   // 将在proxyClassCache中获取loader对应的interfaces的代理类为空时调用，这里使用了策略模式，实际完成创建代理的方法在该类中，所以读者直接看这里即可
   private static final class ProxyClassFactory implements BiFunction<ClassLoader, Class<?>[], Class<?>>{
       // 代理类名前缀名
       private static final String proxyClassNamePrefix = "$Proxy";
       // 用于与前缀名拼接的自增原子性long对象
       private static final AtomicLong nextUniqueNumber = new AtomicLong();

       // 实现代理对象的创建
       public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {
           Map<Class<?>, Boolean> interfaceSet = new IdentityHashMap<>(interfaces.length);
           for (Class<?> intf : interfaces) { // 遍历接口Class数组
               // 使用传递的类加载器加载接口类信息，在加载过程中将会对这些接口的定义进行约束检测
               Class<?> interfaceClass = null;
               try {
                   interfaceClass = Class.forName(intf.getName(), false, loader);
               } catch (ClassNotFoundException e) {
               }
               if (interfaceClass != intf) { // 如果发现传入的类加载器加载的Class对象与传入的接口class对象不相同，说明调用该方法时的类加载器与传入的类加载器不符，这时表明当前接口对传入的类加载器不可见，抛出异常
                   throw new IllegalArgumentException(
                       intf + " is not visible from class loader");
               }
               // 验证该类为接口类型
               if (!interfaceClass.isInterface()) {
                   throw new IllegalArgumentException(
                       interfaceClass.getName() + " is not an interface");
               }
               // 验证没有重复实现同一个接口
               if (interfaceSet.put(interfaceClass, Boolean.TRUE) != null) {
                   throw new IllegalArgumentException(
                       "repeated interface: " + interfaceClass.getName());
               }
           }
           String proxyPkg = null;     // 定义代理类所属包名
           int accessFlags = Modifier.PUBLIC | Modifier.FINAL; // 默认代理类的访问修饰符（public final）
           // 遍历需要实现的接口class实例，记录一个 non-public 代理接口的包，这样就可以在同一个包中定义代理类，同时验证所有 non-public 代理接口都在同一个包中（如果代理的接口为非public的，必然生成的类需要和该接口所在的包相同）
           for (Class<?> intf : interfaces) {
               int flags = intf.getModifiers();
               if (!Modifier.isPublic(flags)) { // 如果找到一个接口不是public共有的，那么修改代理的accessFlags修饰符仅为final
                   accessFlags = Modifier.FINAL;
                   String name = intf.getName();
                   int n = name.lastIndexOf('.');
                   String pkg = ((n == -1) ? "" : name.substring(0, n + 1));
                   if (proxyPkg == null) {
                       proxyPkg = pkg;
                   } else if (!pkg.equals(proxyPkg)) {
                       throw new IllegalArgumentException(
                           "non-public interfaces from different packages");
                   }
               }
           }
           // 如果不是non-public的接口，那么使用com.sun.proxy作为代理类的包名
           if (proxyPkg == null) {
               proxyPkg = ReflectUtil.PROXY_PACKAGE + ".";
           }
           // 生成代理类的名字
           long num = nextUniqueNumber.getAndIncrement();
           String proxyName = proxyPkg + proxyClassNamePrefix + num;
           // 生成代理类的字节文件
           byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
               proxyName, interfaces, accessFlags);
           try {
               // 直接通过defineClass JNI方法进行字节码加载操作，该操作将会返回Class实例
               return defineClass0(loader, proxyName,
                                   proxyClassFile, 0, proxyClassFile.length);
           } catch (ClassFormatError e) {
               throw new IllegalArgumentException(e.toString());
           }
       }
   }
}
```

# ProxyGenerator类原理

前面介绍到我们通过该类的：ProxyGenerator.generateProxyClass( proxyName, interfaces, accessFlags)方法创建代理类的字节码对象，本小节我们来看看该方法的实现过程。流程如下：

1. 创建ProxyGenerator对象
2. 调用ProxyGenerator对象的generateClassFile生成字节码
3. 根据设置环境变量saveGeneratedFiles为true，那么保存在字节码信息

```clike
public static byte[] generateProxyClass(final String name,
                                       Class<?>[] interfaces,
                                       int accessFlags)
{
   // 创建ProxyGenerator对象
   ProxyGenerator gen = new ProxyGenerator(name, interfaces, accessFlags);
   final byte[] classFile = gen.generateClassFile(); // 生成字节码
   // 如果设置环境变量saveGeneratedFiles为true，那么保存在字节码信息
   if (saveGeneratedFiles) {
       java.security.AccessController.doPrivileged(
           new java.security.PrivilegedAction<Void>() {
               public Void run() {
                   try {
                       int i = name.lastIndexOf('.');
                       Path path;
                       if (i > 0) {
                           Path dir = Paths.get(name.substring(0, i).replace('.', File.separatorChar));
                           Files.createDirectories(dir); // 创建目录
                           path = dir.resolve(name.substring(i+1, name.length()) + ".class");
                       } else {
                           path = Paths.get(name + ".class");
                       }
                       Files.write(path, classFile); // 写入字节码信息
                       return null;
                   } catch (IOException e) {
                       throw new InternalError(
                           "I/O exception saving generated file: " + e);
                   }
               }
           });
   }
   return classFile;
}
```

我们看到核心方法为ProxyGenerator对象的generateClassFile方法。我们继续跟进该方法的实现（先看流程实现，然后笔者会在后面详细描述该方法中用到的属性信息）。流程如下：

1. 添加java.lang.Object的hashCode、equals和toString方法
2. 添加代理接口中的方法
3. 验证接口信息，因为我们需要保证相同签名的接口具有相同的返回值
4. 生成代理类中的所有字段和方法的FieldInfo和MethodInfo结构
5. 生成静态初始化方法字节码
6. 验证方法和字段长度为2byte内（JVM规范定义）
7. 按照JVM规范定义，创建内存输出流和数据输出流（数据输出流，可以按照字节数写入，基础很重要哈），来向内存字节数组中写入JVM规范定义的类文件结构（u2表示两个字节，u1表示一个，同理u4也是如此）

```clike
public class ProxyGenerator {
   private static Method hashCodeMethod;
   private static Method equalsMethod;
   private static Method toStringMethod;
   // 在ProxyGenerator的静态方法中，预先获取Object的Method对象
   static {
       try {
           hashCodeMethod = Object.class.getMethod("hashCode");
           equalsMethod =
               Object.class.getMethod("equals", new Class<?>[] { Object.class });
           toStringMethod = Object.class.getMethod("toString");
       } catch (NoSuchMethodException e) {
           throw new NoSuchMethodError(e.getMessage());
       }
   }

   private byte[] generateClassFile() {
       // 添加java.lang.Object的hashCode、equals和toString方法，必须在代理接口的方法之前完成的，以便java.lang.Object中的方法优先于代理接口中的重复方法
       addProxyMethod(hashCodeMethod, Object.class);
       addProxyMethod(equalsMethod, Object.class);
       addProxyMethod(toStringMethod, Object.class);

       // 添加代理接口中的方法
       for (Class<?> intf : interfaces) {
           for (Method m : intf.getMethods()) {
               addProxyMethod(m, intf);
           }
       }
       // 对于每一组具有相同签名的代理方法，验证这些方法的返回类型是否兼容。也即如果有多个具备相同签名的方法，那么必须可以根据返回类型来区分这些方法，比如考虑下：interface foo1{ int foo(); }  interface foo2{ double foo(); } 如果具备完全相同的方法，但是返回类型不是引用类型，也即类型不一致？这时如何代理？答案是只能抛出异常：java.lang.IllegalArgumentException：methods with same signature foo() but incompatible return types: int and others
       for (List<ProxyMethod> sigmethods : proxyMethods.values()) {
           checkReturnTypes(sigmethods);
       }
       // 生成代理类中的所有字段和方法的FieldInfo和MethodInfo结构
       try {
           methods.add(generateConstructor());
           for (List<ProxyMethod> sigmethods : proxyMethods.values()) {
               for (ProxyMethod pm : sigmethods) {
                   // 添加方法静态属性字节码
                   fields.add(new FieldInfo(pm.methodFieldName,
                                            "Ljava/lang/reflect/Method;",
                                            ACC_PRIVATE | ACC_STATIC));

                   // 生成代理方法字节码
                   methods.add(pm.generateMethod());
               }
           }
           // 生成静态初始化方法字节码
           methods.add(generateStaticInitializer());
       } catch (IOException e) {
           throw new InternalError("unexpected I/O Exception", e);
       }
       // 验证方法和字段长度为2byte内（JVM规范定义）
       if (methods.size() > 65535) {
           throw new IllegalArgumentException("method limit exceeded");
       }
       if (fields.size() > 65535) {
           throw new IllegalArgumentException("field limit exceeded");
       }
       // 写入代理类名
       cp.getClass(dotToSlash(className));
       // 写入父类类名
       cp.getClass(superclassName);
       // 写入接口类名
       for (Class<?> intf: interfaces) {
           cp.getClass(dotToSlash(intf.getName()));
       }
       // 设置为只读，以保证禁止在此之后添加新的常量池，因为我们即将写入最终的常量池表
       cp.setReadOnly();
       // 构建内存字节输出流对象，用于存放生成的字节码信息
       ByteArrayOutputStream bout = new ByteArrayOutputStream();
       DataOutputStream dout = new DataOutputStream(bout);
       try {
           // 写入魔术变量 u4 magic;
           dout.writeInt(0xCAFEBABE);
           // 写入 u2 minor_version 最小版本兼容信息
           dout.writeShort(CLASSFILE_MINOR_VERSION);
           // 写入 major_version 主版本号
           dout.writeShort(CLASSFILE_MAJOR_VERSION);
           // 写入常量池信息
           cp.write(dout);
           // 写入 u2 access_flags 访问修饰符
           dout.writeShort(accessFlags);
           // 写入 this_class 当前类信息
           dout.writeShort(cp.getClass(dotToSlash(className)));
           // 写入 super_class 父类信息
           dout.writeShort(cp.getClass(superclassName));
           // 写入 u2 interfaces_count 接口数量
           dout.writeShort(interfaces.length);
           // 写入 interfaces[interfaces_count] 接口实现信息
           for (Class<?> intf : interfaces) {
               dout.writeShort(cp.getClass(
                   dotToSlash(intf.getName())));
           }
           // 写入 u2 fields_count 属性数量
           dout.writeShort(fields.size());
           // 写入  field_info fields[fields_count] 属性信息
           for (FieldInfo f : fields) {
               f.write(dout);
           }
           // 写入 u2 methods_count 方法数量
           dout.writeShort(methods.size());
           // 写入 method_info methods[methods_count] 方法信息
           for (MethodInfo m : methods) {
               m.write(dout);
           }
           // 写入 u2 attributes_count 属性信息，不过我们这里没有类文件的属性信息，所以设置为0
           dout.writeShort(0);

       } catch (IOException e) {
           throw new InternalError("unexpected I/O Exception", e);
       }

       // 返回生成的字节码信息
       return bout.toByteArray();
   }
}
```

我们看到其实在generateClassFile方法中就是校验加写入字节码，那么如何写入的字节码呢？以及用到的ProxyGenerator核心属性，我们需要进行详细讲解。定义了如下几个信息：

1. 定义了最小版本号和主版本号信息
2. 定义生成代理类需要使用的常量池标签
3. 定义生成代理类需要使用的访问修饰符变量
4. 定义生成代理类需要使用的字节码信息
5. 定义代理类的父类类名
6. 定义Proxy类中的handler变量名
7. 代理类名
8. 代理类访问修饰符
9. 代理类常量池信息
10. 代理类方法信息

```clike
public class ProxyGenerator {
   // 定义了最小版本号和主版本号信息
   private static final int CLASSFILE_MAJOR_VERSION = 49;
   private static final int CLASSFILE_MINOR_VERSION = 0;

   // 定义生成代理类需要使用的常量池标签
   private static final int CONSTANT_UTF8              = 1;
   private static final int CONSTANT_UNICODE           = 2;
   private static final int CONSTANT_INTEGER           = 3;
   private static final int CONSTANT_FLOAT             = 4;
   private static final int CONSTANT_LONG              = 5;
   private static final int CONSTANT_DOUBLE            = 6;
   private static final int CONSTANT_CLASS             = 7;
   private static final int CONSTANT_STRING            = 8;
   private static final int CONSTANT_FIELD             = 9;
   private static final int CONSTANT_METHOD            = 10;
   private static final int CONSTANT_INTERFACEMETHOD   = 11;
   private static final int CONSTANT_NAMEANDTYPE       = 12;

   // 定义生成代理类需要使用的访问修饰符变量
   private static final int ACC_PUBLIC                 = 0x00000001;
   private static final int ACC_PRIVATE                = 0x00000002;
   private static final int ACC_STATIC                 = 0x00000008;
   private static final int ACC_FINAL                  = 0x00000010;
   private static final int ACC_SUPER                  = 0x00000020;

   // 定义生成代理类需要使用的字节码信息
   private static final int opc_aconst_null            = 1;
   private static final int opc_iconst_0               = 3;
   private static final int opc_bipush                 = 16;
   private static final int opc_sipush                 = 17;
   private static final int opc_ldc                    = 18;
   private static final int opc_ldc_w                  = 19;
   // ...字节码个数1byte，太多这里省略N个字节码定义

   // 定义代理类的父类类名
   private final static String superclassName = "java/lang/reflect/Proxy";
   // 定义Proxy类中的handler变量名
   private final static String handlerFieldName = "h";
// 代理类名
   private String className;
   // 代理类访问修饰符
   private int accessFlags;
   // 代理类常量池信息
   private ConstantPool cp = new ConstantPool();
   // 代理类属性信息
   private List<FieldInfo> fields = new ArrayList<>();
   // 代理类方法信息
   private List<MethodInfo> methods = new ArrayList<>();
   // 方法签名字符串与ProxyMethod方法描述对象的映射，注意：相同描述符（方法名+入参类型+返回类型），但是相同方法名+参数类型的方法，将会由多个ProxyMethod描述
   private Map<String, List<ProxyMethod>> proxyMethods = new HashMap<>();
   // ProxyMethod的个数，用于生成方法属性名
   private int proxyMethodCount = 0;


   private static class ConstantPool {
       // 常量池实体
       private List<Entry> pool = new ArrayList<>(32);
       // 将所有类型的常量池数据映射到常量池索引,用于查找现有Entry的索引下标
       private Map<Object,Short> map = new HashMap<>(16);
       // 只读标记位
       private boolean readOnly = false;
   }

   // 属性信息
   private class FieldInfo {
       public int accessFlags; // 访问修饰符
       public String name; // 属性名
       public String descriptor; // 描述符
   }
   // 方法信息
   private class MethodInfo {
       public int accessFlags; // 访问修饰符
       public String name; // 方法名
       public String descriptor;// 方法描述符
       public short maxStack; // 方法使用的操作数栈大小
       public short maxLocals; // 方法使用的局部变量表大小
       public ByteArrayOutputStream code = new ByteArrayOutputStream(); // 方法信息的输出字节流
       public List<ExceptionTableEntry> exceptionTable =
           new ArrayList<ExceptionTableEntry>(); // 方法异常分派表
       public short[] declaredExceptions; // 方法声明的可能抛出的异常信息索引（常量池索引下标）
   }

   // 代理方法包装对象
   private class ProxyMethod {
       public String methodName; // 方法名
       public Class<?>[] parameterTypes; // 参数类型
       public Class<?> returnType; // 返回类型
       public Class<?>[] exceptionTypes; // 异常类型
       public Class<?> fromClass; // 方法所属类对象
       public String methodFieldName; // 方法所在类的属性名（ "m" + proxyMethodCount++）
   }
}
```

通过以上的变量描述，相信不用笔者多描述，读者应该也能猜出个一二了，是的，就是根据传入的接口实例，校验过后通过生成的ConstantPool、FieldInfo、MethodInfo对象来完成字节码的构建，写入过程严格按照JVM的规范来进行。额，我想读者是不是也有点好奇心，咋写的？这样，笔者给出ProxyMethod写入字节码生成MethodInfo方法信息的过程来给充满好奇心的读者给以满足~

```clike
private class ProxyMethod {
   private MethodInfo generateMethod() throws IOException {
       String desc = getMethodDescriptor(parameterTypes, returnType); // 通过参数类型+返回类型来构建方法描述符
       MethodInfo minfo = new MethodInfo(methodName, desc, ACC_PUBLIC | ACC_FINAL); // 创建方法元数据对象，并指定方法修饰符为public final
       int[] parameterSlot = new int[parameterTypes.length]; // 构建存放入参的slot
       int nextSlot = 1;
       // 构建入参parameterSlot的大小描述
       for (int i = 0; i < parameterSlot.length; i++) {
           parameterSlot[i] = nextSlot;
           nextSlot += getWordsPerType(parameterTypes[i]);
       }
       int localSlot0 = nextSlot;
       short pc, tryBegin = 0, tryEnd;
       // 构建数据输出流，用于存放方法的字节码信息
       DataOutputStream out = new DataOutputStream(minfo.code);
       // 写入局部变量的this引用到输出流中，也即aload 0
       code_aload(0, out);
       // 写入getfiled字节码
       out.writeByte(opc_getfield);
       // 写入getfiled字节码后面跟随属性描述符
       out.writeShort(cp.getFieldRef(superclassName, handlerFieldName, "Ljava/lang/reflect/InvocationHandler;"));
       ... // 同样省略一万字，因为再往下就是JVM字节码的信息了，这似乎与Proxy的原理偏离了，读者了解到这里即可，对于字节码的原理，笔者会在另外一片文章中给出
       return minfo;
   }
}
```

总结

JDK的动态代理类过程如下：

1. 验证代理类实现接口信息

2. 构建代理类的方法名：proxyClassNamePrefix = "$Proxy" + AtomicLong nextUniqueNumber

3. 创建ProxyGenerator类生成代理类的Class信息，在该类的generateProxyClass方法中，实现流程如下：

4. 1. 构建内存字节输出流并用数据输出流包装该流对象
2. 构建ConstantPool、FieldInfo、MethodInfo对象信息
3. 将ConstantPool、FieldInfo、MethodInfo对象信息按照JVM规范，写入到数据输出流中