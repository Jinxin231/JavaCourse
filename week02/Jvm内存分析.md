#### Parallel GC

并行垃圾收集器对年轻代使用 标记-复制(mark-copy) 算法，对老年代使用 标记-清除-整理(mark-sweep-compact) 算法。年轻代和老年代的垃圾回收时都会触发STW事件，暂停所有的应用线程，再来执行垃圾收集。在执行 标记 和 复制/整理 阶段时都使用多个线程，因此得名“ Parallel ”。通过多个GC线程并行执行的方式，能使JVM在多CPU平台上的GC时间大幅减少。

通过命令行参数 -XX:ParallelGCThreads=NNN 可以指定 GC 线程的数量，其默认值为CPU内核数量。

**并行垃圾收集器适用于多核服务器，其主要目标是增加系统吞吐量(也就是降低GC总体消耗的时间)**。为了达成这个目标，会尽量使用尽可能多的CPU资源：

- 在GC事件执行期间，所有 CPU 内核都在并行地清理垃圾，所以暂停时间相对来说更短

- 在两次GC事件中间的间隔期，不会启动GC线程，所以这段时间内不会消耗任何系统资源

另一方面，因为并行GC的所有阶段都不能中断，所以并行GC很可能会出现长时间的卡顿。长时间卡顿的意思，就是并行GC启动后，一次性完成所有的GC操作，所以单次暂停的时间较长。假如系统延迟是非常重要的性能指标，那么就应该选择其他垃圾收集器。

**启动参数：-Xms1g -Xmx1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseParallelGC**

##### GC日志片段1

```bash
2021-05-12T11:36:11.831+0800: [GC (Allocation Failure) [PSYoungGen: 153056K->43817K(232960K)] 743764K->666812K(932352K), 0.0178427 secs] [Times: user=0.11 sys=0.09, real=0.02 secs]
```

1.<font color='red'>2021-05-12T11:36:11.831</font> GC开始时间

2.<font color='red'>GC (Allocation Failure)</font>  用来区分 Minor GC 还是 Full GC 的标志。这里是一次 小型GC(Minor GC) 。以及发生GC的原因。

3.<font color='red'>[PSYoungGen: 153056K->43817K(232960K)]</font>  PSYoungGen垃圾收集器的名称，表示的是在年轻代中使用的：并行的 标记-复制(mark-copy) ，全线暂停(STW) 垃圾收集器。 153056K->43817K(232960K) 表示GC前后的年轻代使用量，以及年轻代的总大小，简单计算GC后的年轻代使用率 43817K / 232960K= 18.8% 。

4.<font color='red'>743764K->666812K(932352K), 0.0178427 secs</font> GC前后整个堆内存的使用量以及GC所花费的时间。666812K/932352K = 71.51%

5.<font color='red'>[Times: user=0.11 sys=0.09, real=0.02 secs]</font>  GC事件的持续时间，通过三个部分来衡量： user 表示GC线程所消耗的总CPU时间， sys 表示操作系统调用和系统等待事件所消耗的时间； real 则表示应用程序实际暂停的时间。因为并不是所有的操作过程都能全部并行，所以在Parallel GC 中， real 约等于 user + system /GC线程数 。 

通过以上日志可以算出老年代GC后的使用率  <font color='red'> (666812K - 43817K) /(932352K  - 232960K) = 89.07%</font>,使用率高，fullGC即将被触发。

> 总结：年轻代GC，我们可以关注暂停时间，以及GC后的内存使用率是否正常，但不用特别关注GC前
> 的使用量，而且只要业务在运行，年轻代的对象分配就少不了，回收量也就不会少



##### GC日志片段2

```bash
2021-05-12T11:36:11.849+0800: [Full GC (Ergonomics) [PSYoungGen: 43817K->0K(232960K)] [ParOldGen: 622995K->327502K(699392K)] 666812K->327502K(932352K), [Metaspace: 3894K->3894K(1056768K)], 0.0736844 secs] [Times: user=0.39 sys=0.00, real=0.07 secs] 
```

1.<font color='red'>2021-05-12T11:36:11.849</font> GC开始时间

2.<font color='red'>Full GC</font>  表明本次GC清理年轻代和老年代， Ergonomics 是触发GC的原因，表示JVM内部环境认为此时可以进行一次垃圾收集。

3.<font color='red'>[PSYoungGen: 43817K->0K(232960K)</font> 清理年轻代的垃圾收集器是名为 “PSYoungGen” 的STW收集器，采用 标记-复制(mark-copy) 算法。年轻代使用量从 43817K变为 0 ，一般 Full GC 中年轻代的结果都是这样。

4.<font color='red'>ParOldGen</font> 用于清理老年代空间的垃圾收集器类型。在这里使用的是名为 ParOldGen 的垃圾收集器，这是一款并行 STW垃圾收集器，算法为 标记-清除-整理(mark-sweep-compact) 。622995K->327502K(699392K) – 在GC前后老年代内存的使用情况以及老年代空间大小。简单计算一下，GC之前，老年代使用率为 622995K/699392K= 89.07% ，GC后老年代使用率 327502K/699392K= 46.82% ，确实回收了不少。

5.<font color='red'>666812K->327502K(932352K)</font> 在垃圾收集之前和之后堆内存的使用情况，以及可用堆内存的总容量。

6.<font color='red'>[Metaspace: 3894K->3894K(1056768K)], 0.0736844 secs]</font>  元数据区GC情况，可以看到并没有回收元数据区

7.<font color='red'>0.0736844 secs</font> GC持续的时间

8.<font color='red'>[Times: user=0.39 sys=0.00, real=0.07 secs]</font> 同上

>  适用场景：注重吞吐量与CPU资源敏感的场合，与Parallel Scavenge 收集器搭配使用，jdk7和jdk8默认使用该收集器作为老年代收集器。使用参数进行指定

#### CMS

并发标记清除垃圾收集器，设计目标是避免在老年代GC时出现长时间的卡顿。默认情况下，CMS 使用的并发线程数等于CPU内核数的 1/4。

**启动参数 -Xms1g -Xmx1g -XX:+PrintGCDetails -XX:+UseConcMarkSweepGC**

##### GC日志片段1

```bash
2021-05-11T10:26:20.573+0800: [GC (Allocation Failure) 2021-05-11T10:26:20.573+0800: [ParNew: 314559K->34944K(314560K), 0.0349540 secs] 363317K->158890K(1013632K), 0.0350131 secs] [Times: user=0.09 sys=0.14, real=0.04 secs] 
```

1. <font color='red'>2021-05-11T10:26:20.573</font> --- GC开始时间

2. <font color='red'>GC (Allocation Failure)</font> --- Minor GC(小型GC)。Allocation Failure表示本次GC触发原因，是由于年轻代可用空间不足，新对象的内存分配失败引起的。

3. <font color= 'red'>[ParNew: 314559K->34944K(314560K), 0.0349540 secs] </font> --- ParNew为垃圾收集器的名称，在年轻代中
   使用并行的标记-复制(mark-copy) 算法，专门设计了用来配合 CMS 垃圾收集器，因为CMS只负责回收老年代。后面的数字表示GC前后的年轻代使用量变化，以及年轻代的总大小。0.0349540 secs为消耗的时间。

4. <font color= 'red'>363317K->158890K(1013632K), 0.0350131 secs</font> --- 表示GC前后堆内存的使用量变化，以及堆内存空间的大小。消耗的时间是  0.0350131 secs 。

5. <font color= 'red'>[Times: user=0.09 sys=0.14, real=0.04 secs]</font>  --- GC事件持续时间。user是GC线程消耗的时间； sys 是操作系统调用和系统等待事件消耗的时间； 应用程序实际暂停的时间 real ~= (user + sys)/GC线程数 。

   进一步计算

   GC前，年轻代使用率为 314559K/314560K = 99.99%。堆使用率 363317K/1013632K = 35.84%。老年代的使用率为：(363317 - 314559)/(1013632  -314560 ) = 6.9%。

   GC后，年轻代使用率为 34944K/314560K = 11.10%。堆使用率 158890K/1013632K = 15.67%。晋升老年代内存：(314559K - 34944K) - (363317K - 158890K) =  75188K

​      

##### GC日志片段2

```bash
2021-05-11T10:26:20.871+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 354994K(699072K)] 395812K(1013632K), 0.0002309 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-11T10:26:20.872+0800: [CMS-concurrent-mark-start]
2021-05-11T10:26:20.874+0800: [CMS-concurrent-mark: 0.003/0.003 secs] [Times: user=0.03 sys=0.01, real=0.00 secs] 
2021-05-11T10:26:20.874+0800: [CMS-concurrent-preclean-start]
2021-05-11T10:26:20.876+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-11T10:26:20.876+0800: [CMS-concurrent-abortable-preclean-start]
2021-05-11T10:26:20.914+0800: [GC (Allocation Failure) 2021-05-11T10:26:20.914+0800: [ParNew: 314560K->34943K(314560K), 0.0462988 secs] 669554K->467515K(1013632K), 0.0463480 secs] [Times: user=0.28 sys=0.05, real=0.05 secs] 
2021-05-11T10:26:21.004+0800: [GC (Allocation Failure) 2021-05-11T10:26:21.004+0800: [ParNew: 314559K->34944K(314560K), 0.0485643 secs] 747131K->547519K(1013632K), 0.0486102 secs] [Times: user=0.33 sys=0.05, real=0.05 secs] 
2021-05-11T10:26:21.095+0800: [GC (Allocation Failure) 2021-05-11T10:26:21.095+0800: [ParNew: 314560K->34942K(314560K), 0.0483611 secs] 827135K->627673K(1013632K), 0.0484112 secs] [Times: user=0.27 sys=0.05, real=0.05 secs] 
2021-05-11T10:26:21.185+0800: [GC (Allocation Failure) 2021-05-11T10:26:21.185+0800: [ParNew: 314558K->34943K(314560K), 0.0497874 secs] 907289K->706991K(1013632K), 0.0498339 secs] [Times: user=0.41 sys=0.02, real=0.05 secs] 
2021-05-11T10:26:21.235+0800: [CMS-concurrent-abortable-preclean: 0.009/0.359 secs] [Times: user=1.44 sys=0.16, real=0.36 secs] 
2021-05-11T10:26:21.235+0800: [GC (CMS Final Remark) [YG occupancy: 35231 K (314560 K)]2021-05-11T10:26:21.235+0800: [Rescan (parallel) , 0.0004274 secs]2021-05-11T10:26:21.235+0800: [weak refs processing, 0.0002653 secs]2021-05-11T10:26:21.236+0800: [class unloading, 0.0002563 secs]2021-05-11T10:26:21.236+0800: [scrub symbol table, 0.0004295 secs]2021-05-11T10:26:21.236+0800: [scrub string table, 0.0001262 secs][1 CMS-remark: 672047K(699072K)] 707279K(1013632K), 0.0015772 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-11T10:26:21.237+0800: [CMS-concurrent-sweep-start]
2021-05-11T10:26:21.238+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-11T10:26:21.238+0800: [CMS-concurrent-reset-start]
2021-05-11T10:26:21.240+0800: [CMS-concurrent-reset: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
```

上图展示的是一次fullGC的过程

1.Initial Mark(初始标记)

   这个阶段伴随着STW暂停，初始标记的目标是标记所有的根对象，包括 GC ROOT 直接引用的对象，以及被年轻代中所有存活对象所引用的对象。

```bash
2021-05-11T10:26:20.871+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 354994K(699072K)] 395812K(1013632K), 0.0002309 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
```

- <font color='red'>2021-05-11T10:26:20.871</font> Initial Mark发生的时间点
- <font color='red'>[1 CMS-initial-mark: 354994K(699072K)]</font> 表示老年代的使用量，以及老年代的空间大小。<font color='red'>使用率50.78%已经触发了fullGC</font>（目前存疑）。
- <font color='red'>395812K(1013632K), 0.0002309 secs</font>   当前堆内存的使用量，以及可用堆的大小、消耗的时间。可以看出这个时间非常短，只有 0.2毫秒左右，因为要标记的这些Root数量很少。
- <font color='red'>[Times: user=0.00 sys=0.00, real=0.00 secs] </font>  初始标记事件暂停的时间,可以忽略不计

2.Concurrent Mark(并发标记)

  在并发标记阶段，CMS 从前一阶段 “Initial Mark” 找到的 ROOT 开始算起，遍历老年代并标记所有的存活对象。

```java
2021-05-11T10:26:20.872+0800: [CMS-concurrent-mark-start]
2021-05-11T10:26:20.874+0800: [CMS-concurrent-mark: 0.003/0.003 secs] [Times: user=0.03 sys=0.01, real=0.00 secs] 
```

- <font color='red'>CMS-concurrent-mark-start</font> 表示开始并发标记
- <font color='red'>[CMS-concurrent-mark: 0.003/0.003 secs] </font>  此阶段的持续时间，分别是GC线程消耗的时间和实际消耗的时间。
- <font color='red'>[Times: user=0.03 sys=0.01, real=0.00 secs]</font>  没有太多意义，因为处于并发阶段，只是一个大概的值。

3.Concurrent Preclean(并发预清理)

此阶段同样是与应用线程并发执行的，不需要停止应用线程。

```bash
2021-05-11T10:26:20.874+0800: [CMS-concurrent-preclean-start]
2021-05-11T10:26:20.876+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
```

- <font color='red'>[CMS-concurrent-preclean-start]</font>   表明这是并发预清理阶段的日志，这个阶段会统计前面的并发标记阶段执行过程中发生了改变的对象。
- <font color='red'>[CMS-concurrent-preclean: 0.001/0.001 secs]</font>  此阶段的持续时间，分别是GC线程消耗的时间和实际消耗的时间。
- <font color='red'>[Times: user=0.00 sys=0.00,real=0.00 secs ] </font> 没有太多意义，因为处于并发阶段，只是一个大概的值。

4.Concurrent Abortable Preclean(可取消的并发预清理)

此阶段也不停止应用线程，尝试在会触发STW 的 Final Remark 阶段开始之前，尽可能地多干一些活。本阶段的具体时间取决于多种因素，因为它循环做同样的事情，直到满足某一个退出条件(如迭代次数，有用工作量，消耗的系统时间等等)

```bash
2021-05-11T10:26:20.876+0800: [CMS-concurrent-abortable-preclean-start]
2021-05-11T10:26:20.914+0800: [GC (Allocation Failure) 2021-05-11T10:26:20.914+0800: [ParNew: 314560K->34943K(314560K), 0.0462988 secs] 669554K->467515K(1013632K), 0.0463480 secs] [Times: user=0.28 sys=0.05, real=0.05 secs] 
2021-05-11T10:26:21.004+0800: [GC (Allocation Failure) 2021-05-11T10:26:21.004+0800: [ParNew: 314559K->34944K(314560K), 0.0485643 secs] 747131K->547519K(1013632K), 0.0486102 secs] [Times: user=0.33 sys=0.05, real=0.05 secs] 
2021-05-11T10:26:21.095+0800: [GC (Allocation Failure) 2021-05-11T10:26:21.095+0800: [ParNew: 314560K->34942K(314560K), 0.0483611 secs] 827135K->627673K(1013632K), 0.0484112 secs] [Times: user=0.27 sys=0.05, real=0.05 secs] 
2021-05-11T10:26:21.185+0800: [GC (Allocation Failure) 2021-05-11T10:26:21.185+0800: [ParNew: 314558K->34943K(314560K), 0.0497874 secs] 907289K->706991K(1013632K), 0.0498339 secs] [Times: user=0.41 sys=0.02, real=0.05 secs] 
2021-05-11T10:26:21.235+0800: [CMS-concurrent-abortable-preclean: 0.009/0.359 secs] [Times: user=1.44 sys=0.16, real=0.36 secs] 
```

- <font color='red'>[CMS-concurrent-abortable-preclean-start]</font>   指示此阶段的名称：“Concurrent AbortablePreclean”。
- <font color='red'>[CMS-concurrent-abortable-preclean: 0.009/0.359 secs]</font> – 此阶段GC线程的运行时间和实际占用的时间。从本质上讲，GC线程试图在执行 STW 暂停之前等待尽可能长的时间。默认条件下，此阶段可以持续最长5秒钟的时间。
- <font color='red'> [Times: user=0.00 sys=0.00,real=0.00 secs]</font>  对并发阶段来说没多少意义，因为程序在并发阶段中持续运行。

**此阶段结束后，需要进入Final Remark阶段，Final Remark需要STW,CMS号称是停顿时间最短的GC，如此长的停顿时间肯定是不能接受的。那么需要一种机制能快速的识别新生代和老年代或者的对象。**

- **新生代：新生代垃圾回收完剩下的对象全是活着的，并且活着的对象很少。如果在识别新生代前进行Minor GC，那么情况应该会好很多。concurrent-abortable-preclean 会重复地以迭代的方式执行，直到满足退出条件。**

​     **中断条件，配置参数是`-XX:CMSScheduleRemarkEdenPenetration=50`（默认值），表示当 eden 区内存占用到达 50%时，中断 abortable-preclean。**

​      **参照 https://blog.csdn.net/enemyisgodlike/article/details/106960687**

-  **老年代：老年代的机制与一个叫CARD TABLE的东西（这个东西其实就是个数组,数组中每个位置存的是一个byte）密不可分。CMS将老年代的空间分成大小为512bytes的块，card table中的每个元素对应着一个块。并发标记时，如果某个对象的引用发生了变化，就标记该对象所在的块为  dirty card。并发预清理阶段就会重新扫描该块，将该对象引用的对象标识为可达。所以，Minor GC通过扫描card table就可以很快的识别老年代引用新生代。****

5.Final Remark(最终标记)

最终标记阶段是此次GC事件中的第二次(也是最后一次)STW停顿。本阶段的目标是完成老年代中所有存活对象的标记。因为之前的预清理阶段是并发执行的，有可能GC线程跟不上应用程序的修改速度。所以需要一次 STW 暂停来处理各种复杂的情况。通常CMS会尝试在年轻代尽可能空的情况下执行 final remark 阶段，以免连续触发多次 STW 事件。

```bash
2021-05-11T10:26:21.235+0800: [GC (CMS Final Remark) [YG occupancy: 35231 K (314560 K)]2021-05-11T10:26:21.235+0800: [Rescan (parallel) , 0.0004274 secs]2021-05-11T10:26:21.235+0800: [weak refs processing, 0.0002653 secs]2021-05-11T10:26:21.236+0800: [class unloading, 0.0002563 secs]2021-05-11T10:26:21.236+0800: [scrub symbol table, 0.0004295 secs]2021-05-11T10:26:21.236+0800: [scrub string table, 0.0001262 secs][1 CMS-remark: 672047K(699072K)] 707279K(1013632K), 0.0015772 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
```

- <font color='red'>(CMS Final Remark)</font>  该阶段名称
-  <font color='red'>[YG occupancy: 35231 K (314560 K)]</font>  当前年轻代的使用量和总容量。
- <font color='red'>[Rescan (parallel) , 0.0004274 secs]</font>  在程序暂停后进行重新扫描(Rescan)，以完成存活对象的标记。这部分是并行执行的，消耗的时间为 0.0004274秒
-  <font color='red'>[weak refs processing, 0.0002653 secs]</font>  第一个子阶段： 处理弱引用的持续时间
- <font color='red'>[class unloading, 0.0002563 secs]</font>  第二个子阶段： 卸载不使用的类，以及持续时间
- <font color='red'>[scrub symbol table, 0.0004295 secs]</font>  第三个子阶段： 清理符号表，即持有class级别metadata 的符号表(symbol tables)
- <font color='red'>[scrub string table, 0.0001262 secs]</font> 第四个子阶段： 清理内联字符串对应的 stringtables
- <font color='red'>[1 CMS-remark: 672047K(699072K)]</font>   此阶段完成后老年代的使用量和总容量
- <font color='red'>707279K(1013632K)  0.0015772 secs</font>  此阶段完成后，整个堆内存的使用量和总容量
- <font color='red'>[Times: user=0.00 sys=0.00, real=0.00 secs]</font>   GC事件的持续时间

在这5个标记阶段完成后，老年代中的所有存活对象都被标记上了，接下来JVM会将所有不使用的对象清除，以回收老年代空间。

6.Concurrent Sweep(并发清除)

此阶段与应用程序并发执行，不需要STW停顿。目的是删除不再使用的对象，并回收他们占用的内存空间。

```bash
2021-05-11T10:26:21.237+0800: [CMS-concurrent-sweep-start]
2021-05-11T10:26:21.238+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
```

- <font color='red'>[CMS-concurrent-sweep: 0.001/0.001 secs] </font>  阶段名称，以及此阶段的持续时间和实际占用的时间，这是一个四舍五入值，只精确到小数点后3位
- <font color='red'>[Times: user=0.00 sys=0.00, real=0.00 secs] </font> “Times”部分对并发阶段来说没有多少意义，因为是从并发标记开始时计算的，而这段时间内不仅是并发标记线程在执行，程序线程也在运行。

7.Concurrent Reset(并发重置)

此阶段与应用程序线程并发执行，重置CMS算法相关的内部数据结构，下一次触发GC时就可以直接使用。

```bash
2021-05-11T10:26:21.238+0800: [CMS-concurrent-reset-start]
2021-05-11T10:26:21.240+0800: [CMS-concurrent-reset: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
```

- <font color='red'>[CMS-concurrent-reset: 0.002/0.002 secs] </font>  此阶段的名称，“Concurrent Reset”，重置CMS算法的内部数据结构，为下一次GC循环做准备。以及此阶段的持续时间和实际占用的时间。
- <font color='red'>[Times: user=0.00 sys=0.00, real=0.00 secs] </font> “Times”部分对并发阶段来说没多少意义，因为是从并发标记开始时计算的，而这段时间内不仅GC线程在运行，程序也在运行。

CMS垃圾收集器在减少停顿时间上做了很多给力的工作，很大一部分GC线程是与应用线程并发运行的，不需要暂停应用线程，这样就可以在一般情况下每次暂停的时候较少。当然，CMS也有一些缺点，其中最大的问题就是老年代的内存碎片问题，在某些情况下GC会有不可预测的暂停时间，特别是堆内存较大的情况下。

> 优点：并发收集，低停顿
>
> 缺点：
>
> - CMS收集器对CPU资源非常敏感，CMS默认启动对回收线程数(CPU数量+3)/4，当CPU数量在4个以上时，并发回收时垃圾收集线程不少于25%，并随着CPU数量的增加而下降，但当CPU数量不足4个时，对用户影响较大。
> - CMS无法处理浮动垃圾，可能会出现“Concurrent Mode Failure”失败而导致一次FullGC的产生。这时会启动后备预案，临时用SerialOld来重新进行老年代的垃圾收集。由于CMS并发清理阶段用户线程还在运行，伴随程序运行自然还会有新的垃圾产生，这部分垃圾出现在标记过程之后，CMS无法在当次处理掉，只能等到下一次GC，这部分垃圾就是浮动垃圾。同时也由于在垃圾收集阶段用户线程还需要运行，那也就需要预留足够的内存空间给用户线程使用，因此CMS收集器不能像其他老年代几乎完全填满再进行收集。可以通过参数-XX:CMSInitiatingOccupancyFraction修改CMS触发的百分比。
> - 因为CMS采用的是标记清除算法，因此垃圾回收后会产生空间碎片。通过参数可以进行优化。
>
> ```objectivec
> -XX:UserCMSCompactAtFullCollection #开启碎片整理（默认是开的）
> 
> -XX:CMSFullGCsBeforeCompaction #执行多少次不压缩的Full GC之后，跟着来一次压缩的Full GC
> ```
>
> - 适用场景：重视服务器响应速度，要求系统停顿时间最短。可以使用参数-XX:+UserConMarkSweepGC来选择CMS作为老年代回收器。

#### G1

G1的全称是Garbage-First，意为垃圾优先，哪一块的垃圾最多就优先清理它。

##### **Evacuation Pause: young(纯年轻代模式转移暂停)**

当年轻代空间用满后，应用线程会被暂停，年轻代内存块中的存活对象被拷贝到存活区。 如果还没有存活区，则任意选择一部分空闲的内存块作为存活区。拷贝的过程称为转移(Evacuation)，这和前面介绍的其他年轻代收集器是一样的工作原理。

```bash
2021-05-13T09:11:59.846+0800: [GC pause (G1 Evacuation Pause) (young), 0.0212513 secs]
   [Parallel Time: 20.1 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 1813.4, Avg: 1814.7, Max: 1824.3, Diff: 10.9]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.9]
      [Update RS (ms): Min: 0.0, Avg: 0.2, Max: 0.2, Diff: 0.2, Sum: 1.4]
         [Processed Buffers: Min: 0, Avg: 2.4, Max: 4, Diff: 4, Sum: 19]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 8.9, Avg: 18.1, Max: 19.6, Diff: 10.6, Sum: 144.5]
      [Termination (ms): Min: 0.0, Avg: 0.2, Max: 0.4, Diff: 0.4, Sum: 1.7]
         [Termination Attempts: Min: 1, Avg: 1.3, Max: 3, Diff: 2, Sum: 10]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.2]
      [GC Worker Total (ms): Min: 9.0, Avg: 18.6, Max: 20.0, Diff: 11.0, Sum: 148.8]
      [GC Worker End (ms): Min: 1833.3, Avg: 1833.3, Max: 1833.4, Diff: 0.1]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 1.0 ms]
      [Choose CSet: 0.1 ms]
      [Ref Proc: 0.2 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.2 ms]
      [Humongous Reclaim: 0.1 ms]
      [Free CSet: 0.2 ms]
   [Eden: 157.0M(157.0M)->0.0B(187.0M) Survivors: 18.0M->22.0M Heap: 410.7M(1024.0M)->270.8M(1024.0M)]
 [Times: user=0.03 sys=0.08, real=0.02 secs] 
```

1.<font color='red'>[GC pause (G1 Evacuation Pause) (young), 0.0212513 secs]</font> G1转移暂停，纯年轻代模式; 只清理年轻代空间。持续的系统时间为0.0212513秒 ，也就是 21ms 

2.<font color='red'>[Parallel Time: 20.1 ms, GC Workers: 8]</font>  表明后面的活动由8个 Worker 线程并行执行，消耗时间为20.1毫秒(real time); worker 是一种模式，类似于一个老板指挥多个工人干活

​    省略部分下面详细分析

3.<font color='red'>[Code Root Fixup: 0.0 ms]</font>  释放用于管理并行活动的内部数据，一般都接近于零。这个过程是串行执行的。

4.<font color='red'>[Code Root Purge: 0.0 ms]</font> 清理其他部分数据，也是非常快的，如非必要基本上等于零。也是串行执行的过程。

5.<font color='red'>[Other: 1.0 ms]</font> 其他活动消耗的时间，其中大部分是并行执行的。

6.<font color='red'>Eden: 157.0M(157.0M)->0.0B(187.0M)</font> 暂停之前和暂停之后，Eden 区的使用量/总容量。

7.<font color='red'>Survivors: 18.0M->22.0M</font> GC暂停前后，存活区的使用量

8.<font color='red'>Heap: 410.7M(1024.0M)->270.8M(1024.0M)</font> 暂停前后，整个堆内存的使用量与总容量

9.<font color='red'>[Times: user=0.03 sys=0.08, real=0.02 secs]</font> GC事件的持续时间。说明：系统时间(wall clock time/elapsed time)，是指一段程序从运行到终止，系统时钟走过的时间。一般系统时间都要比CPU时间略微长一点。

省略的关键部分：

```bash
  [Parallel Time: 20.1 ms, GC Workers: 8]
   [GC Worker Start (ms): Min: 1813.4, Avg: 1814.7, Max: 1824.3, Diff: 10.9]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.9]
      [Update RS (ms): Min: 0.0, Avg: 0.2, Max: 0.2, Diff: 0.2, Sum: 1.4]
      [Processed Buffers: Min: 0, Avg: 2.4, Max: 4, Diff: 4, Sum: 19]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 8.9, Avg: 18.1, Max: 19.6, Diff: 10.6, Sum: 144.5]
      [Termination (ms): Min: 0.0, Avg: 0.2, Max: 0.4, Diff: 0.4, Sum: 1.7]
         [Termination Attempts: Min: 1, Avg: 1.3, Max: 3, Diff: 2, Sum: 10]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.2]
      [GC Worker Total (ms): Min: 9.0, Avg: 18.6, Max: 20.0, Diff: 11.0, Sum: 148.8]
      [GC Worker End (ms): Min: 1833.3, Avg: 1833.3, Max: 1833.4, Diff: 0.1]
```

1. <font color='red'>[Parallel Time: 20.1 ms, GC Workers: 8]</font> 表明下列活动由8个线程并行执行，消耗的时间为20.1 毫秒(real time)。

2. <font color='red'>[GC Worker Start (ms): Min: 1813.4, Avg: 1814.7, Max: 1824.3, Diff: 10.9]</font> GC的worker线程开始启动时，相对于 pause 开始时间的毫秒间隔。如果 Min 和 Max 差别很大，则表明本机其他进程所使用的线程数量过多，挤占了GC的可用CPU时间。

3. <font color='red'>[Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.9]</font> 用了多长时间来扫描堆外内存(non-heap)的 GC ROOT，如classloaders，JNI引用，JVM系统ROOT等。后面显示了运行时间，“Sum” 指的是CPU时间。

4. <font color='red'>[Update RS (ms): Min: 0.0, Avg: 0.2, Max: 0.2, Diff: 0.2, Sum: 1.4]</font> RS 是Remembered Set 的缩写，可以参考前面章节

   <font color='red'>[Processed Buffers: Min: 0, Avg: 2.4, Max: 4, Diff: 4, Sum: 19]</font>

   <font color='red'>[Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]</font>

5. <font color='red'>[Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]</font> 扫描实际代码中的 root 用了多长时间：例如线程栈中的局部变量。

6. <font color='red'>[Object Copy (ms): Min: 8.9, Avg: 18.1, Max: 19.6, Diff: 10.6, Sum: 144.5]</font> 用了多长时间来拷贝回收集中的存活对象

7. <font color='red'>[Termination (ms): Min: 0.0, Avg: 0.2, Max: 0.4, Diff: 0.4, Sum: 1.7]</font> GC的worker线程用了多长时间来确保自身可以安全地停止，在这段时间内什么也不做，完成后GC线程就终止运行了，所以叫终止等待时间。

8. <font color='red'>[Termination Attempts: Min: 1, Avg: 1.3, Max: 3, Diff: 2, Sum: 10]</font> GC的worker 线程尝试多少次 try 和 teminate。如果worker发现还有一些任务没处理完，则这一次尝试就是失败的，暂时还不能终止

9. <font color='red'>[GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.2]</font> 其他的小任务， 因为时间很短，在GC日志将他们归结在一起

10. <font color='red'>[GC Worker Total (ms): Min: 9.0, Avg: 18.6, Max: 20.0, Diff: 11.0, Sum: 148.8]</font> GC的worker 线程工作时间总计

11. <font color='red'>[GC Worker End (ms): Min: 1833.3, Avg: 1833.3, Max: 1833.4, Diff: 0.1]</font> GC的worker 线程完成作业时刻，相对于此次GC暂停开始时间的毫秒数。通常来说这部分数字应该大致相等，否则就说明有太多的线程被挂起，很可能是因为“坏邻居效应(noisy neighbor)" 所导致的

##### **阶段1：Concurrent Marking(并发标记)**

可以在 Evacuation Pause 日志中的第一行看到(initial-mark)暂停，类似这样:

```bash
2021-05-13T09:12:00.069+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark), 0.0147589 secs]
   [Parallel Time: 13.9 ms, GC Workers: 8]
```

这里引发GC的原因是大对象分配，也可能是其他原因，例如： to-space exhausted ，或者默认GC原因等等

##### 阶段 2: Root Region Scan(Root区扫描)

此阶段标记所有从 "根区域" 可达的存活对象。根区域包括：非空的区域，以及在标记过程中不得不收集的区域。

```bash
2021-05-13T09:12:00.084+0800: [GC concurrent-root-region-scan-start]
2021-05-13T09:12:00.105+0800: [GC concurrent-root-region-scan-end, 0.0206720 secs]
```

##### 阶段 3: Concurrent Mark(并发标记)

```bash
2021-05-13T09:12:00.105+0800: [GC concurrent-mark-start]
2021-05-13T09:12:00.108+0800: [GC concurrent-mark-end, 0.0028788 secs]
```

##### 阶段 4: Remark(再次标记)

```bash
2021-05-13T09:12:00.108+0800: [GC remark 2021-05-13T09:12:00.108+0800: [Finalize Marking, 0.0001352 secs] 2021-05-13T09:12:00.108+0800: [GC ref-proc, 0.0000872 secs] 2021-05-13T09:12:00.108+0800: [Unloading, 0.0005101 secs], 0.0014257 secs]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
```

##### 阶段 5: Cleanup(清理)

最后这个清理阶段为即将到来的转移阶段做准备，统计小堆块中所有存活的对象，并将小堆块进行排序，以提升GC的效率。此阶段也为下一次标记执行必需的所有整理工作(house-keeping activities)：维护并发标记的内部状态。要提醒的是，所有不包含存活对象的小堆块在此阶段都被回收了。有一部分任务是并发的: 例如空堆区的回收，还有大部分的存活率计算，此阶段也需要一个短暂的STW暂停，才能不受应用线程的影响并完成作业。

这种STW停顿的对应的日志如下：

```bash
2021-05-13T09:12:00.109+0800: [GC cleanup 569M->561M(1024M), 0.0005364 secs]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-13T09:12:00.110+0800: [GC concurrent-cleanup-start]
2021-05-13T09:12:00.110+0800: [GC concurrent-cleanup-end, 0.0000235 secs]
```

标记周期一般只在碰到region中一个存活对象都没有的时候，才会顺手处理一把，大多数情况下都不释放内
存。

##### Evacuation Pause (mixed)(转移暂停: 混合模式)

并发标记完成之后，G1将执行一次混合收集(mixed collection)，不只清理年轻代，还将一部分老年代区域也加入到 collection set 中。混合模式的转移暂停(Evacuation pause)不一定紧跟并发标记阶段。在并发标记与混合转移暂停之间，很可能会存在多次 young 模式的转移暂停。

> 混合模式 就是指这次GC事件混合着处理年轻代和老年代的region。这也是G1等增量垃圾收集器的特色。
> 而ZGC等最新的垃圾收集器则不使用分代算法。 当然，以后可能还是会实现分代的，毕竟分代之后性能还会有提升。

```bash
2021-05-13T09:12:00.237+0800: [GC pause (G1 Evacuation Pause) (mixed), 0.0134361 secs]
   [Parallel Time: 12.6 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 2204.1, Avg: 2204.1, Max: 2204.2, Diff: 0.2]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.8]
      [Update RS (ms): Min: 0.1, Avg: 0.2, Max: 0.2, Diff: 0.1, Sum: 1.5]
         [Processed Buffers: Min: 2, Avg: 2.8, Max: 4, Diff: 2, Sum: 22]
      [Scan RS (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 11.9, Avg: 12.0, Max: 12.1, Diff: 0.3, Sum: 95.9]
      [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 1.1]
         [Termination Attempts: Min: 1, Avg: 1.5, Max: 3, Diff: 2, Sum: 12]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [GC Worker Total (ms): Min: 12.4, Avg: 12.5, Max: 12.5, Diff: 0.2, Sum: 99.9]
      [GC Worker End (ms): Min: 2216.6, Avg: 2216.6, Max: 2216.6, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 0.8 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.1 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.1 ms]
      [Humongous Reclaim: 0.1 ms]
      [Free CSet: 0.2 ms]
   [Eden: 6144.0K(6144.0K)->0.0B(49.0M) Survivors: 45.0M->2048.0K Heap: 594.0M(1024.0M)->502.4M(1024.0M)]
 [Times: user=0.06 sys=0.02, real=0.01 secs] 
```

1. <font color='red'>[Update RS (ms): Min: 0.1, Avg: 0.2, Max: 0.2, Diff: 0.1, Sum: 1.5]</font>  因为 Remembered Sets 是并发处理的，必须确保在实际的垃圾收集之前，缓冲区中的 card 得到处理。如果card数量很多，则GC并发线程的负载可能就会很高。可能的原因是修改的字段过多，或者CPU资源受限
2. <font color='red'>[Processed Buffers: Min: 2, Avg: 2.8, Max: 4, Diff: 2, Sum: 22]</font>  各个 worker 线程处理了多少个本地缓冲区(local buffer)。
3.  <font color='red'>[Scan RS (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]</font> 用了多长时间扫描来自RSet的引用。
4.  <font color='red'>[Clear CT: 0.1 ms]</font> 清理 card table 中 cards 的时间。清理工作只是简单地删除“脏”状态，此状态用来标识一个字段是否被更新的，供Remembered Sets使用。
5. <font color='red'>[Redirty Cards: 0.1 ms]</font> 将 card table 中适当的位置标记为 dirty 所花费的时间。"适当的位置"是由GC本身执行的堆内存改变所决定的，例如引用排队等



#####  Full GC (Allocation Failure)

G1是一款自适应的增量垃圾收集器。一般来说，只有在内存严重不足的情况下才会发生Full GC。 比如堆空间不足或者to-space空间不足。在前面的示例程序基础上，增加缓存对象的数量，即可模拟Full GC。

```bash
2021-05-14T11:48:41.731+0800: [Full GC (Allocation Failure)  455M->333M(512M), 0.0559124 secs]
   [Eden: 0.0B(25.0M)->0.0B(32.0M) Survivors: 0.0B->0.0B Heap: 455.0M(512.0M)->333.3M(512.0M)], [Metaspace: 3894K->3894K(1056768K)]
 [Times: user=0.05 sys=0.00, real=0.06 secs] 
```

因为我们的堆内存空间很小，存活对象的数量也不多，所以这里看到的FullGC暂停时间很短。在堆内存较大的情况下【8G+】，如果G1发生了FullGC，暂停时间可能会退化，达到几十秒甚至更多。

> 适用场景：要求尽可能可控 GC 停顿时间；内存占用较大的应用。可以用 -XX:+UseG1GC 使用 G1 收集器，jdk9 默认使用 G1 收集器

#### GC选择的经验总结

|      收集器      | 串行、并行、并发 | 新生代/老年代 |        算法        |     目标     |                使用场景                |
| :--------------: | :--------------: | ------------- | :----------------: | :----------: | :------------------------------------: |
|      Serial      |       串行       | 新生代        |      复制算法      | 响应速度优先 |        单CPU环境下的Client模式         |
|    Serial Old    |       串行       | 老年代        |     标记-整理      | 响应速度优先 | 单CPU环境下的Client模式、CMS的后备预案 |
|      ParNew      |       并行       | 新生代        |      复制算法      | 响应速度优先 |   多CPU环境时在Server模式下与CMS配合   |
| Paralle Scavenge |       并行       | 新生代        |      复制算法      |  吞吐量优先  |    在后台运算而不需要太多交互的任务    |
|   Parallel Old   |       并行       | 老年代        |     标记-整理      |  吞吐量优先  |    在后台运算而不需要太多交互的任务    |
|       CMS        |       并发       | 老年代        |     标记-清除      | 响应速度优先 |  集中在互联网站B/S服务端 上的Java应用  |
|        G1        |       并发       | both          | 标记-整理+复制算法 | 响应速度优先 |      面向服务端应用，将来替换CMS       |

