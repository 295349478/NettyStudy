package cn.zhang.netty.c4.d1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.zhang.netty.ByteBufferUtil.debugAll;

/**
 * @ClassName Server
 * @Description TODO
 * @Author ZPH
 * @Date 2022/9/14 15:34
 * @Version 1.0
 */
public class MuliThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("Boss");
        //创建服务器channel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        //创建选择器
        Selector boss = Selector.open();
        //注册获取
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        //1、创建固定数量的Worker 并初始化
        Worker[] workers = new Worker[4];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        //2、创建一个计数器
        AtomicInteger index = new AtomicInteger();
        while (true) {
            //开始监听事件
            boss.select();
            //获取事件触发迭代器集合
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                //获取出发得事件key
                SelectionKey key = iter.next();
                //将处理的事件去除
                iter.remove();

                //处理
                if (key.isAcceptable()) {
                    //获取到被触发的channel（需要关联的channel）
                    SocketChannel sc = (SocketChannel) key.channel();
                    sc.configureBlocking(false);
                    //创建工作线程以及他的选择器(使用取模运算)
                    workers[index.getAndIncrement() % workers.length].register(sc);
                }

            }
        }
    }

    static class Worker implements Runnable {
        private Thread thread;
        private Selector selector;
        private String name;

        //使用队列做到两个线程间的通信
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }


        public void register(SocketChannel sc) throws IOException {
            if (thread == null) {
                synchronized (Worker.class) {
                    if (thread == null) {
                        thread = new Thread(this, name);
                        thread.start();
                        selector = Selector.open();
                    }
                }
            }
            queue.add(() -> {
                //这个方法需要等待selector空闲才能使用，所以直接使用队列
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            //唤醒一次selector，让他先执行一次任务，不至于太晚将客户端的channel闲置
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        //执行sc.register(selector, SelectionKey.OP_READ, null);
                        task.run();
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();

                        //这个选择器触发可读事件
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }


                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
