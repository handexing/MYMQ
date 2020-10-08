package com.my.mq.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;

/**
 * @author: handx
 * @description: TODO
 * @create 2020-09-28 17:17
 */
public class ThreadLocalKryoFactory extends KryoFactory {

    private final ThreadLocal<Kryo> holder  = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            return createKryo();
        }
    };

    public Kryo getKryo() {
        return holder.get();
    }

}
