# Verticle 总结
===
1. `Verticle`是个类，通常我们继承`AbstractVerticle`来实现自己的业务。
2. 一个Vert.x程序由一个或多个`Verticle`组成，且必有一个`Verticle`是程序的入口。
3. `Verticle`有生命周期，通常我们关心`start`与`stop`方法。
4. 在一个`Verticle`可以部署其他任意的`Verticle`，也可以让已部署的下架。这些操作都会命中上面说的生命周期方法。
5. `Verticle`除了可以在部署的时候传递参数。也可以通过`EventBus`在任意`Verticle`间用收发消息的方式传递数据。
6. 一个`Verticle`中的代码，永远只会被一个线程访问，这里有三个例外:
    * 自己在代码中手动操作`Thread`
    * 部署`Verticle`时追加设置`setMultiThreaded(true)`（但是这种用法已经被标记为`@deprecated`）
    * 通过`vertx.executeBlocking`执行阻塞式代码时`ordered`参数传`false`
7. 所以，如果按照`Verticle`的使用规范编写代码，大部分情况下不会出现线程不安全的情况。
8. `Verticle`根据应用场景不同，分为两种，一种是普通版，一种是`work`版。简单的说就是比较耗时的阻塞式代码应该放在`work Verticle`中执行。
9. 普通版`Verticle`用的线程池是`eventloop-thread`，而`work`版的线程池叫`worker-thread`。前者是绑定式实用，部署`Verticle`的时候，分配一个线程供它使用，就不会再变了；但是后者不是。
