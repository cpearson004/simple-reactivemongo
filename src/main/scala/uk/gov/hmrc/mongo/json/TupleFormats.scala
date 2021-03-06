/*
 * Copyright 2014 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.hmrc.mongo.json

/**
 * Thanks goes to Alexander Jarvis for his Gist (https://gist.github.com/alexanderjarvis/4595298)
 */
trait TupleFormats {
  import play.api.libs.json._
  import play.api.data.validation._

  implicit def tuple2Reads[B, T1, T2](c : (T1, T2) => B)(implicit aReads: Reads[T1], bReads: Reads[T2]): Reads[B] = Reads[B] {
    case JsArray(arr) if arr.size == 2 => for {
      a <- aReads.reads(arr(0))
      b <- bReads.reads(arr(1))
    } yield c(a, b)
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected array of two elements"))))
  }

  implicit def tuple2Writes[T1, T2](implicit aWrites: Writes[T1], bWrites: Writes[T2]): Writes[Tuple2[T1, T2]] = new Writes[Tuple2[T1, T2]] {
    def writes(tuple: Tuple2[T1, T2]) = JsArray(Seq(aWrites.writes(tuple._1), bWrites.writes(tuple._2)))
  }

  implicit def tuple2Format[T1, T2](implicit aReads: Reads[T1], bReads: Reads[T2], aWrites: Writes[T1], bWrites: Writes[T2]) =
    Format(tuple2Reads[Tuple2[T1, T2], T1, T2]((t1, t2) => (t1, t2)), tuple2Writes[T1, T2])
}

object TupleFormats extends TupleFormats
