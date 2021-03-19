package com.bigzhao.xml2axml.chunks;

import com.bigzhao.xml2axml.IntWriter;
import com.bigzhao.xml2axml.ValueType;

import java.io.IOException;

/**
 * Created by Roy on 15-10-5.
 */
public class AttrChunk extends Chunk<Chunk.EmptyHeader>{
    private StartTagChunk startTagChunk;
    public String prefix;
    public String name;
    public String namespace;
    public String rawValue;

    public AttrChunk(StartTagChunk startTagChunk) {
        super(startTagChunk);
        this.startTagChunk = startTagChunk;
        header.size=20;
    }



    public ValueChunk value = new ValueChunk(this);

    @Override
    public void preWrite() {
        value.calc();
    }

    @Override
    public void writeEx(IntWriter w) throws IOException {
        w.write(startTagChunk.stringIndex(null,namespace));
        w.write(startTagChunk.stringIndex(namespace,name));
        //w.write(-1);
        if (value.type== ValueType.STRING)
            w.write(startTagChunk.stringIndex(null,rawValue));
        else
            w.write(-1);
        value.write(w);
    }
}
