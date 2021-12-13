# 基础

> 作者：[mysticalycc](https://gschaos.club/)
>
> 原文链接：https://gschaos.club/583.html



### **使用“+”可以连接两个字符串（String对象），那么，是怎样进行连接的？**

```java
public class StringTest {
    public static void main(String[] args) {
        final String s = "abc";
        String x = "abc" + "def";
        String y = s + "def";
        String z = x + "abc";
        String z1 = s + "def" + "abc";

        String s1 = "black";
        String s2 = "board";
        String s3 = s1 + s2;
        String s4 = "black" + s2;
        System.out.println("s3==s4 " + (s3 == s4));
        System.out.println(s4.intern() == s3.intern());
    }
}
```

反编译查看：

```bash
  0 ldc #2 <abc>
  2 astore_1
  3 ldc #3 <abcdef>
  5 astore_2
  6 ldc #3 <abcdef>
  8 astore_3
  9 new #4 <java/lang/StringBuilder>
 12 dup
 13 invokespecial #5 <java/lang/StringBuilder.<init> : ()V>
 16 aload_2
 17 invokevirtual #6 <java/lang/StringBuilder.append : (Ljava/lang/String;)Ljava/lang/StringBuilder;>
 20 ldc #2 <abc>
 22 invokevirtual #6 <java/lang/StringBuilder.append : (Ljava/lang/String;)Ljava/lang/StringBuilder;>
 25 invokevirtual #7 <java/lang/StringBuilder.toString : ()Ljava/lang/String;>
 28 astore 4
 30 ldc #8 <abcdefabc>
 32 astore 5
 34 ldc #9 <black>
 36 astore 6
 38 ldc #10 <board>
 40 astore 7
 42 new #4 <java/lang/StringBuilder>
 45 dup
 46 invokespecial #5 <java/lang/StringBuilder.<init> : ()V>
 49 aload 6
 51 invokevirtual #6 <java/lang/StringBuilder.append : (Ljava/lang/String;)Ljava/lang/StringBuilder;>
 54 aload 7
 56 invokevirtual #6 <java/lang/StringBuilder.append : (Ljava/lang/String;)Ljava/lang/StringBuilder;>
 59 invokevirtual #7 <java/lang/StringBuilder.toString : ()Ljava/lang/String;>
 62 astore 8
 64 new #4 <java/lang/StringBuilder>
 67 dup
 68 invokespecial #5 <java/lang/StringBuilder.<init> : ()V>
 71 ldc #9 <black>
 73 invokevirtual #6 <java/lang/StringBuilder.append : (Ljava/lang/String;)Ljava/lang/StringBuilder;>
 76 aload 7
 78 invokevirtual #6 <java/lang/StringBuilder.append : (Ljava/lang/String;)Ljava/lang/StringBuilder;>
 81 invokevirtual #7 <java/lang/StringBuilder.toString : ()Ljava/lang/String;>
 84 astore 9
 86 getstatic #11 <java/lang/System.out : Ljava/io/PrintStream;>
 89 new #4 <java/lang/StringBuilder>
 92 dup
 93 invokespecial #5 <java/lang/StringBuilder.<init> : ()V>
 96 ldc #12 <s3==s4 >
 98 invokevirtual #6 <java/lang/StringBuilder.append : (Ljava/lang/String;)Ljava/lang/StringBuilder;>
101 aload 8
103 aload 9
105 if_acmpne 112 (+7)
108 iconst_1
109 goto 113 (+4)
112 iconst_0
113 invokevirtual #13 <java/lang/StringBuilder.append : (Z)Ljava/lang/StringBuilder;>
116 invokevirtual #7 <java/lang/StringBuilder.toString : ()Ljava/lang/String;>
119 invokevirtual #14 <java/io/PrintStream.println : (Ljava/lang/String;)V>
122 getstatic #11 <java/lang/System.out : Ljava/io/PrintStream;>
125 aload 9
127 invokevirtual #15 <java/lang/String.intern : ()Ljava/lang/String;>
130 aload 8
132 invokevirtual #15 <java/lang/String.intern : ()Ljava/lang/String;>
135 if_acmpne 142 (+7)
138 iconst_1
139 goto 143 (+4)
142 iconst_0
143 invokevirtual #16 <java/io/PrintStream.println : (Z)V>
146 return
```

结论：

> 当字符串拼接的前后可以确定为常量（final修饰或者直接字符串）则不使用stringbuilder，直接确定结果
>
> 当字符串拼接前后有一个不为常量则使用stringbuilder.append拼接

------

## 隐式转换+浮点类型

```java
    public static void main(String[] args) {

        final short s =30;
        byte b = s;
        System.out.println(b);

        char a = (char)b;
        int i = a;
        System.out.println(i);

        float f1 = 3000000;
        BigDecimal f1b = new BigDecimal(f1);
        float f2 =f1+1;
        BigDecimal f2b = new BigDecimal(f2);
        System.out.println(f1==f2);

        System.out.println(f1b);
        System.out.println(f2b);
    }
```

运行结果

```bash
30
30
false
3000000
3000001
```

稍微修改代码：

```java
    public static void main(String[] args) {
        //改为负数
        final short s =-30;
        byte b = s;
        System.out.println(b);

        char a = (char)b;
        int i = a;
        System.out.println(i);

        //增加一个0
        float f1 = 30000000;
        BigDecimal f1b = new BigDecimal(f1);
        float f2 =f1+1;
        BigDecimal f2b = new BigDecimal(f2);
        System.out.println(f1==f2);

        System.out.println(f1b);
        System.out.println(f2b);
    }
```

运行结果：

```bash
-30
65506
true
30000000
30000000
byte     1字节               
short    2字节               
int      4字节               
long     8字节               
char     2字节（C语言中是1字节）可以存储一个汉字
float    4字节               
double   8字节               
boolean  false/true(理论上占用1bit,1/8字节，实际处理按1byte处理)       
```

char类型是无符号类型（0～65535），因此char与byte（−128～127），char与short（−32768～32767）类型不存在子集关系，也就是说，char与其他两种类型之间的转换总是需要类型转换。

整型数据（byte、short、char、int、long 5种类型）间的扩展转换，如果操作数是有符号的，扩展时就进行有符号扩展，扩展位为符号位。如果操作数是无符号的，则扩展时进行无符号扩展，扩展位为0。整型数据间的收缩转换，只是进行简单的截断，保留目标类型的有效位（即丢弃所有高位）。

**float类型在Java中占用4字节，long类型在Java中占用8字节，为什么float类型的取值范围比long类型的取值范围还大？**

在Java中，浮点类型的结构与运算符合IEEE754标准。浮点类型使用符号位、指数与有效位数（尾数）来表示。其中，符号位用来表示浮点值的正负，指数位用来存储指数值，有效位数用来存储小数值。在Java中，浮点类型float与double的结构如表：

![image-20210825094215398](https://y.gschaos.club/pic/java_jichu.png)

其中，符号位为0，浮点值为正，符号位为1，浮点值为负。浮点类型的指数与有效位数都是无符号的，指数采用了偏移量方式来存储指数值，偏移量为2x −1（比实际指数大2x −1），其中x为指数域的位数，float类型为8位，double类型为11位。例如，浮点值float类型值8.1f的指数为3，在指数位中实际存储的值为127 +3，即130。任意一个非0并且非无穷大的浮点数v都可以表示成v = s × m × 2e的形式。s为1或−1，m为有效位数（小数），e为指数。

在计算机中，所能存储的两个临近小数之间的差值，就是浮点数值的间隙，我们可以使用Math类的ulp方法来取得这个间隙值，浮点之间的间隙是随着浮点值的绝对值增大而增大的，当浮点数的绝对值很大时，间隙也会很大，对浮点数进行一个较小的增量，无法使浮点值改变。

当一个值A不能够准确地由浮点类型（float或double）表示时，就会使用最接近的，并且可以使用浮点类型表示的值来代替值A。代替的标准采用最近舍入模式。

如果值A位于可用浮点类型表示的两个相邻值B与C之间

查看间隙：

```java
  public static void main(String[] args) {
        //改为负数
        final short s =-30;
        byte b = s;
        System.out.println(b);

        char a = (char)b;
        int i = a;
        System.out.println(i);

        //增加一个0
        float f1 = 30000000;
        /*打印f1的间隙， 在间隙除以2的闭区间内的数不会增大原始数据，即会近似取最近的数据表示。
        */
        System.out.println("间隙："+Math.ulp(f1));
        BigDecimal f1b = new BigDecimal(f1);
        float f2 =f1+1;

        BigDecimal f2b = new BigDecimal(f2);
        System.out.println(f1==f2);

        System.out.println(f1b);
        System.out.println(f2b);
    }
```

运行结果：

```bash
-30
65506
间隙：2.0
间隙：2.0
true
30000000
30000000
```

------

## i++与++i到底有什么不同？仅仅是先加与后加的区别吗？

```java
    public static void main(String[] args) {
        int spi=16;
        int spi2 =++spi;
        System.out.println(spi);

        int sd =16;
        sd=sd++;
        System.out.println(sd);
    }
```

运行结果：

```bash
17
16
```

使用javap -c 查看：

```java
public class com.company.Main {
  public com.company.Main();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: bipush        16                 //将16压入操作数栈
       2: istore_1                          //弹出操作数栈的首位并保存在局部变量的1位置(spi=)
       3: iinc          1, 1                //局部变量1位置进行+1操作
       6: iload_1                           //将局部变量1压入操作数栈
       7: istore_2                          //弹出操作数栈的首位并保存在局部变量的2位置(spi2=)
       8: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      11: iload_1               
      12: invokevirtual #13                 // Method java/io/PrintStream.println:(I)V
      15: bipush        16                  //将16压入操作数栈
      17: istore_3                          //弹出操作数栈的首位并保存在局部变量的3位置(sd=)
      18: iload_3                           //将局部变量3压入操作数栈  
      19: iinc          3, 1                //将局部变量3进行+1操作
      22: istore_3                          //弹出操作数栈的首位并保存在局部变量的3位置(sd=)
      23: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
      26: iload_3
      27: invokevirtual #13                 // Method java/io/PrintStream.println:(I)V
      30: return
}
```

从0--> 6 和 15-->19 可以得到

> ++i 操作是先局部变量+1再压栈操作，
>
> i++ 是直接压栈后局部变量自+1

i++的操作直接导致操作栈数没有进行+1操作；可以认为是个临时变量。

所以得出：



当前内容已被隐藏，您需要登录才能查看

登录立刻注册



例子：

```java
        int[] str = {0,0,0,0,0};
        int index=1;
        str[++index]=index++;
        System.out.println(Arrays.toString(str));
```

运行结果：

```bash
[0, 0, 2, 0, 0]
```

虽然赋值运算符是从右向左结合的，但是操作数的确定是从左向右的，也就是在赋值操作发生前.

运算前会先将左侧的操作数保存起来，左侧的操作数不会受到其右侧表达式的影响而造成改变.

例子：

```java
      public static void main(String[] args) {  
        int[] str = {0,0,0,0,0};
        int index=1;
        str[++index]=index++;
        System.out.println(Arrays.toString(str));
        test1(index,++index,index=2);
        test1(index=5,index++,index);
    }

    public static void test1(int a,int b ,int c){
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }
```

运行结果

```bash
[0, 0, 2, 0, 0]342556
```

### 变量交换

**一个变量x异或另一个变量y两次，结果的值为x。**

如下：

```java
/**
     * 中间变量交换
     * @param v
     */
    public void swap1(Value v) {
        int temp = v.x;
        v.x = v.y;
        v.y = temp;
    }

    /**
     * 加法交换，即便溢出最终结果也是正确的。
     * @param v
     */
    public void swap2(Value v) {
        v.x = v.x+v.y;
        v.y = v.x-v.y;
        v.x=v.x-v.y;
    }

    /**
     *  v^y^y = x
     * @param v
     */
    public void swap3(Value v) {
        v.x = v.x^v.y;
        v.y = v.x^v.y;
        v.x=v.x^v.y;
    }

    /**
     * 减法交换
     * @param v
     */
    public void swap4(Value v) {
        v.x = v.x-v.y;
        v.y = v.x+v.y;
        v.x=v.y-v.x;
    }

class Value {
    public int x;
    public int y;

}
```

### 开关选择表达式switch的类型内幕

- switch表达式可以是byte、short、char、int、Byte、Short、Character、Integer、String或枚举类型。
- case表达式必须是常量表达式或枚举常量名，并且其类型可以赋值给switch表达式类型
- switch表达式的类型为基本数据类型的包装类型时，将包装类型拆箱为基本数据类型。
- 当switch类型为枚举类型时，会创建一个匿名类来辅助完成。
- 当switch类型为String类型时，将switch语句拆分为两个switch语句，分别对String对象的哈希码及临时变量来辅助完成。

例子：

```java
  public static void main(String[] args) {
        String swstr="test";

        switch (swstr){
            case "test":
                System.out.println("test");
                break;
            case "test1":
                System.out.println("test1");
                break;
            default:
                System.out.println("null");
        }
    }
```

使用javap -c 查看反编译代码：

```java
 Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: ldc           #7                  // String test
       2: astore_1
       3: aload_1
       4: astore_2
       5: iconst_m1
       6: istore_3
       7: aload_2
       8: invokevirtual #9                  // Method java/lang/String.hashCode:()I
      11: lookupswitch  { // 2
               3556498: 36
             110251487: 50
               default: 61
          }
      36: aload_2
      37: ldc           #7                  // String test
      39: invokevirtual #15                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z
      42: ifeq          61
      45: iconst_0
      46: istore_3
      47: goto          61
      50: aload_2
      51: ldc           #19                 // String test1
      53: invokevirtual #15                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z
      56: ifeq          61
      59: iconst_1
      60: istore_3
      61: iload_3
      62: lookupswitch  { // 2
                     0: 88
                     1: 99
               default: 110
          }
      88: getstatic     #21                 // Field java/lang/System.out:Ljava/io/PrintStream;
      91: ldc           #7                  // String test
      93: invokevirtual #27                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
      96: goto          118
      99: getstatic     #21                 // Field java/lang/System.out:Ljava/io/PrintStream;
     102: ldc           #19                 // String test1
     104: invokevirtual #27                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     107: goto          118
     110: getstatic     #21                 // Field java/lang/System.out:Ljava/io/PrintStream;
     113: ldc           #33                 // String null
     115: invokevirtual #27                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
     118: return
}
```

大致意思如下：

```java
 public static void main(String[] args) {
        String swstr = "test";
        String swstr1 = s;
        byte byte0 = -1;
        switch (swstr1.hashCode()) {
            case 3556498:
                if (swstr1.equals("test")) {
                    byte0 = 0;
                }
                break;
            case 110251487:
                if (swstr1.equals("test1")) {
                    byte0 = 1;
                }
                break;
        }
        switch (byte0) {
            case 0:
                System.out.println("test");
                break;
            case 1:
                System.out.println("test1");
                break;
            default:
                System.out.println("null");
        }
    }
```

### string 极限值解析说明

对于这个问题，就要说到Java源文件编译生成的class文件。在class文件中，使用CONSTANT_Utf8_info表来存放各种常量字符串，包括String字面常量，类或接口的全限定名，方法及变量的名称、描述符等。CONSTANT_Utf8_info表的结构如表

![image-20210825105105555](https://y.gschaos.club/pic/image-20210825105105555.png)

CONSTANT_Utf8_info表使用2字节来表示字符串的长度，因此，bytes数组的最大长度为216−1，即65535字节。

可是，为什么4个字符（“A”、“á”、“字”与“㊣”）的运行结果各不相同呢？原因在于，在CONSTANT_Utf8_info表中，从“\u0001”～“\u007f”，bytes使用1字节来表示，空字符（null，即“\u0000”）和从“\u0080”～“\u07ff”，使用2字节来表示，从“\u0800”～“\uffff”，使用3字节来表示，而对于增补字符，即代码点范围在“U+10000”～“U+10FFFF”之间的字符，使用6字节来表示。也可以这样认为，增补字符是使用一个代理对来表示的，而代理对的取值范围为“\ud800”～“\udfff”，这些字符都在“\u0800”～“\uffff”之间，每个代理字符使用3字节表示，共6字节。

上述的存储是在class文件中的实现，不要与Java程序中的字符相混淆，对于Java程序来说，“A”、“á”、“字”都使用一个char类型变量表示，即2字节，而“[插图]”（增补字符）使用两个char类型变量表示，即4字节。

> String字面常量的最大长度与String在内存中的最大长度是不一样的，后者的最大长度为int类型的最大值，即2147483647，而前者根据字符（字符Unicode值）的不同，最大长度也不同，最大长度为65534（可手动修改class文件，令输出结果为65535）。
>
> String字面常量的最大长度是由CONSTANT_Utf8_info表来决定的，该长度在编译时确定，如果超过了CONSTANT_Utf8_info表bytes数组所能表示的上限，就会产生编译错误。

### ==与equals

1. 从Object类继承的equals方法与“==”运算符的比较方式是相同的。如果继承的equals方法对我们自定义的类不适用，则可以重写equals方法。
2. 重写equals方法的时候，需要遵守5点规定，否则该类与其他类（例如实现了Collection接口或其子接口的类）交互时，很可能产生不确定的运行结果。
    1. 自反性。对于任何非null的引用值x，x.equals(x)应返回true。
    2. 对称性。对于任何非null的引用值x与y，当且仅当：y.equals(x)返回true时，x.equals(y)才应返回true。
    3. 传递性。对于任何非null的引用值x、y与z，如果x.equals(y)返回true，并且y.equals(z)返回true，那么x.equals(z)也应返回true
    4. 一致性。对于任何非空引用值x与y，假设对象上equals比较中的信息没有被修改，则多次调用x.equals(y)始终返回true或始终返回false。
    5. 对于任何非空引用值x，x.equals(null)应返回false。
3. 在重写equals方法的同时，也必须要重写hashCode方法。否则该类与其他类（例如实现了Map接口或其子接口的类）交互时，很可能产生不确定的运行结果。
4. 重写hashCode方法时也要遵守3点规定，其中第3点规定是建议性的。
    1. 在Java应用程序执行期间，如果在对象equals方法比较中所用的信息没有被修改，那么在同一对象上多次调用hashCode方法时，必须一致地返回相同的整数。但如果多次执行同一个应用时，不要求该整数必须相同。
    2. 如果两个对象通过调用equals方法是相等的，那么这两个对象调用hashCode方法必须返回相同的整数。
    3. 如果两个对象通过调用equals方法是不相等的，不要求这两个对象调用hashCode方法必须返回不同的整数。但是，程序员应该意识到对不同的对象产生不同的哈希码值可以提高哈希表的性能。

### 字面常量到String常量池

1. String类维护一块特殊的区域，称为常量池。因为String对象是不可改变的，因此没有必要创建两个相同的String对象。只需将String对象加入常量池，在需要的时候取出，这样即可实现String对象的共享
2. 在程序中出现String编译时常量（String字面常量与String常量表达式）时，会自动调用intern方法，如果常量池中含有相等的String对象，则直接返回常量池中的对象，否则将对象加入常量池中并返回该对象。
3. 对于运行时创建的String对象（非String编译时常量），会分配到堆中，系统不会自动调用intern方法拘留该对象，不过我们依然可以自行调用该对象的intern方法对该对象进行拘留。

### 重载

1. 当两个或多个方法的名称相同，而参数列表不同时，这几个方法就构成了重载。重载方法可以根据参数列表对应的类型与参数的个数来区分，但是，参数的名称、方法的返回类型、方法的异常列表与类型参数不能作为区分重载方法的条件。
2. 究竟选择哪个方法调用，顺序是这样的
    1. 在第1阶段，自动装箱（拆箱）与可变参数不予考虑，搜索对应形参类型可以匹配实参类型并且形参个数与实参个数相同的方法。
    2. 如果在步骤1中不存在符合条件的方法，在第2阶段，自动装箱与拆箱将会执行。
    3. 如果在步骤2中不存在符合条件的方法，在第3阶段，可变参数的方法将会考虑。
    4. 如果3个阶段都没有搜索到符合条件的方法，将会产生编译错误。如果符合条件的方法多于一个，将会选择最明确的方法。最明确的方法定义为：如果A方法的形参列表类型对应的都可以赋值给B方法的形参列表类型，则A方法比B方法明确。如果无法选出最明确的方法，则会产生编译错误
3. 当方法的参数类型是类型变量时，可以首先将类型变量进行擦除，然后与普通类型的调用规则相同。
4. 法重载不同于方法重写。调用哪个重载方法是根据实参的静态类型（编译时类型）决定的，与运行时实参的具体类型无关。

### 重写

1. 方法重写不同于方法重载，方法重载是根据实参的静态类型来决定调用哪个方法，而重写是根据运行时引用所指向对象的实际类型来决定调用哪个方法。
2. 在方法是静态还是实例方面，方法重写要求父类与子类的方法都是实例方法，如果其中有一个方法是静态方法，则会产生编译错误，如果两个方法都是静态方法，没有编译错误，但这种情况是方法隐藏，不是方法重写。
3. 在方法签名方面，方法重写要求子类方法签名是父类方法签名的子签名。
4. 在方法的返回类型方面，方法重写要求子类方法返回类型是父类方法返回类型的可替换类型。
5. 在方法的返回类型方面，方法重写要求子类方法返回类型是父类方法返回类型的可替换类型。
6. 在方法的异常列表方面，方法重写要求子类方法不能比父类方法抛出更多的受检异常（但可以抛出更多的非受检异常），否则就会在调用方法的位置无法成功捕获。
7. 在方法的继承方面，方法重写要求子类继承了父类的方法，即父类的方法在子类中必须是可访问的。如果子类没有继承父类的方法，则父类的方法在子类中不可访问，自然也就不可能重写父类的方法。

### 方法与成员变量的隐藏

1. 静态方法不能重写，只可以隐藏。
2. 成员变量也不能重写，只可以隐藏。相对于方法的隐藏，成员变量的隐藏只要求父类与子类的成员变量名称相同，并且父类的成员变量在子类中可见即可。与成员变量的访问权限、类型、实例变量还是静态变量无关。
3. 重写与隐藏的本质区别是：重写是动态绑定的，根据运行时引用所指向对象的实际类型来决定调用相关类的成员。而隐藏是静态绑定的，根据编译时引用的静态类型来决定调用相关类的成员。换句话说，如果子类重写了父类的方法，当父类的引用指向子类对象时，通过父类的引用调用的是子类的方法。如果子类隐藏了父类的方法（成员变量），通过父类的引用调用的仍然是父类的方法（成员变量）。

### 构造方法

构造器，也称构造方法，用来初始化类的实例成员变量，在使用new关键字创建对象的时候，由系统自动调用。构造器必须与类名相同，并且没有返回值，在外观上与类中声明的方法相似，例如，也可以具备形式参数、类型变量、异常列表等。然而，构造器不是方法，也不是类的成员。

1. 构造器不是方法，也不是类的成员。因此，子类不能继承父类的构造器
2. · 构造器是递归调用的，子类的构造器会调用父类的构造器，直到调用到Object类的构造器为止。
3. 构造器没有创建对象，构造器是使用new创建对象时由系统自动调用的，用来初始化类的实例成员。从顺序上来说，是先创建对象，然后才调用构造器的。
4. 当类中没有显式地声明构造器时，编译器会自动添加一个无参的构造器，该构造器的访问权限与类的访问权限相同。默认的构造器体并不为空，该构造器会调用父类的无参构造器，并可能执行实例成员变量的初始化。
5. protected构造器与包访问权限构造器是不同的，前者可以在子类的构造器中使用super来调用，而后者不能。
6. 在构造器或是实例方法调用的时候，会将其所关联的对象作为第1个参数隐式传递，这个对象就是我们在构造器或实例方法中使用的当前对象this，静态方法没有关联对象，因此也不会隐式传递对象。

```java
  public static void main(String[] args) {
         Object o = new Object();
        NullCall nullCall = null;
        nullCall.m();
    }

class NullCall{
    public static void m(){
        System.out.println("m()");
    }
}
```

运行结果：

```bash
m()
```

### 成员变量不同的初始化方式

1. 成员变量在创建时，系统会为其分配一个默认值，布尔类型为false，字符类型为‘\u0000’，整数类型为0，浮点类型为0.0，引用类型（包括数组类型）为null。局部变量不管是什么类型的，都无默认值，在使用局部变量的值时一定要先对局部变量进行初始化。
2. 实例变量可以在声明处初始化，也可以在实例初始化块或构造器中初始化。静态变量可以在声明处或静态初始化块中初始化
3. 当子类继承父类的实例变量x，如果子类没有隐藏变量x，则对于同一个对象，只存在一个变量x，即通过this.x与super.x访问的是同一个变量。如果子类隐藏变量x，则通过this.x与super.x访问的将不再是同一个变量。
4. 当子类继承父类的静态变量x，如果子类没有隐藏变量x，则x由父类以及所有子类所共享，无论是通过类名（父类或子类）还是对象名（父类对象或子类对象）访问的x，都是同一个变量。如果子类隐藏变量x，则通过父类（父类名或父类对象）访问的x与通过子类（子类名或子类对象）访问的x将不再是同一个变量。

### 初始化顺序和向前引用

1. 初始化的顺序可以简单总结为先静态，后实例，先父类，后子类。对于静态初始化，按照静态变量声明处初始化与静态初始化块在类中出现的顺序执行。对于实例初始化，按照实例变量声明处初始化与实例初始化块在类中出现的顺序执行，然后执行构造器。
2. 当心潜在的向前引用，如果使用一个尚未初始化的变量值，就可能得到错误的结果。
3. 在构造器中不要调用可由子类重写的方法，调用private与final的方法才是安全的。
4. 对于值为编译时常量的final变量，可以认为这样的变量会最先得到初始化，我们在程序中无法观察到其默认值，即使向前引用这种类型的变量也是如此。

向前引用例子：

```java
public static void main(String[] args) {
        ParentX parentX = new SubToY();
    }

class ParentX{
    public String kind ="parent";

    @Override
    public String toString(){

        return kind;
    }

    public ParentX(){
        System.out.println(toString());
    }
}

class SubToY extends ParentX{
    public String color ="sub";
    public String kind ="sub";
    @Override
    public String toString(){

        return "super.kind = "+super.toString()+" ,this.color="+color+" ,this.kind="+kind;
    }

}
```

运行结果：

```bash
super.kind = parent ,this.color=null ,this.kind=null
```

### 加载，链接，初始化

1. 可以调用ClassLoader类的loadClass方法加载一个类（接口），类（接口）在加载后不会初始化。
2. 类的加载使用的是双亲委派模型，即当类加载器加载某个类时，首先委派双亲加载器加载该类。
3. Java类库中的类是由启动类加载器所加载的，而我们自定义的类通常是由系统类加载器所加载的。
4. 类中的实例变量声明处初始化与实例初始化块可以认为被复制到构造器最上方执行，编译器会为类中的每个构造器生成一个＜init＞方法，也会为静态初始化（包括静态变量声明处初始化与静态初始化块）生成一个＜clinit＞方法（如果存在静态初始化语句）。
5. 理解类与接口初始化的时刻，在什么情况下初始化，在什么情况下不会初始化。
6. 分清主动使用与被动使用，被动使用的时候，不会初始化被动使用关联的类（接口）。