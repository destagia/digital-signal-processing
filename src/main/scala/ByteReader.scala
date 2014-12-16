package jp.kobe.util

import java.io.File;
import java.io.FileInputStream;
import java.nio._

class ByteReader(val path :File){

    val buf=new Array[Byte](path.length.asInstanceOf[Int])
    val io=new FileInputStream(path)
    io.read(buf)
    io.close()

    // 追記。getFloatに必要だった
    val bb=ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN)

    def pos :Int=bb.position
    def isEnd :Boolean=bb.position>=bb.capacity

    def getString(length :Int) :String={
        val ret=new String(buf, bb.position, length)
        bb.position(bb.position()+length)
        ret
    }
    def get() :Int={
        val b=bb.get()
        if(b>=0) b else b+256
    }
    def toList:List[Float] = {
        if (isEnd) {
            Nil
        } else {
            getFloat() :: toList
        }
    }
    def getWORD() :Int={
        val b0=get()
        val b1=get()
        (b1<<8) + b0
    }
    def getDWORD() :Int={
        val b0=get()
        val b1=get()
        val b2=get()
        val b3=get()
        (b3<<24) + (b2<<16) + (b1<<8) + b0
    }
    def getFloat() :Float=bb.getFloat()
}