package com.neo.sk.breaker.front.utils.byteObject

import com.neo.sk.breaker.front.utils.byteObject.decoder.{BytesDecoder, DecoderFailure}
import com.neo.sk.breaker.front.utils.byteObject.encoder.BytesEncoder
import com.neo.sk.breaker.shared.ptcl.util.MiddleBuffer

/**
  * User: Taoz
  * Date: 7/16/2018
  * Time: 10:47 AM
  */
object ByteObject {


  implicit final class EncoderOps[A](val wrappedEncodeable: A) extends AnyVal {
    final def fillMiddleBuffer[B <: MiddleBuffer](
      buffer: B
    )(implicit encoder: BytesEncoder[A]): B = {
      buffer.clear()
      encoder.encode(wrappedEncodeable, buffer)
      buffer
    }
  }

  def bytesDecode[A](input: MiddleBuffer)(implicit decoder: BytesDecoder[A]): Either[DecoderFailure, A] = {
    decoder.decode(input)
  }


}
