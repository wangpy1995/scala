package parallel

import scala.collection.mutable

/**
  * Created by wpy on 17-1-10.
  */
class HeapSort[A, B, S <: Iterable[A]](f: A => B)(implicit ord: Ordering[B]) {
  /**
    * 对l排序返回排序后的Seq
    *
    * @param l    待排序集合的迭代器
    * @param desc 降/升序(默认为true,降序)
    * @return
    */
  def sort(l: S, desc: Boolean = true) = HeapSort.sort(f)(l, 0, desc)

  /**
    * 对l排序并返回前top个结果
    *
    * @param l    待排序集合的迭代器
    * @param top  返回最多结果数目
    * @param desc 降/升序(默认为true,降序)
    * @return
    */
  def top(l: S, top: Int, desc: Boolean = true) = HeapSort.sort(f)(l, top, desc)

  /**
    * 对可变集合排序，返回排序后的Seq
    *
    * @param l    待排序可变集合的迭代器
    * @param desc 降/升序(默认为true,降序)
    * @return
    */
  def sort_m[M <: mutable.Seq[A]](l: M, desc: Boolean = true) = HeapSort.sort_mutable(f)(l, 0, desc)

  /**
    * 对可变集合l排序并返回前top个结果
    *
    * @param l    待排序可变集合的迭代器
    * @param top  返回最多结果数目
    * @param desc 降/升序(默认为true,降序)
    * @return
    */
  def top_m[M <: mutable.Seq[A]](l: M, top: Int, desc: Boolean = true) = HeapSort.sort_mutable(f)(l, top, desc)

  /**
    * 对可变集合l并行排序并返回前top个结果
    *
    * @param l    待排序可变集合的迭代器
    * @param top  返回最多结果数目
    * @param desc 降/升序(默认为true,降序)
    * @return
    */
  def top_m_par[M <: mutable.Seq[A]](l: M, top: Int, desc: Boolean = true) = HeapSort.top_mutable_par(f)(l, top, desc)

  /**
    * 对可变集合l的指定范围排序并返回排序后的Seq
    *
    * @param seq   待排序可变集合
    * @param top   返回最多结果数目
    * @param desc  降/升序(默认为true,降序)
    * @param from  待排序的起始位置
    * @param until 待排序的结束位置
    * @return
    */
  def sort_range[M <: mutable.Seq[A]](seq: M, top: Int, desc: Boolean = true)(from: Int = 0, until: Int = seq.length) = HeapSort.sort_mutableRange(f)(seq, top, desc)(from, until)

  /**
    * 对seq中两个已经排序的区段进行合并排序，将src合并到dst
    *
    * @param seq  可变集合
    * @param src  待合并的源区段(起始位置，结束位置)
    * @param dst  待合并的目标区段(起始位置，结束位置)
    * @param desc 降/升序(默认为true,降序)
    * @return
    */
  def merge2Seq(seq: mutable.Seq[A], src: (Int, Int), dst: (Int, Int), desc: Boolean = true) = HeapSort.merge2Seq(f)(seq, src, dst, desc)

  /**
    * 对seq中两个已经排序的区段进行合并排序，将src合并到dst
    *
    * @param seq  可变集合
    * @param src  待合并的源区段(起始位置，结束位置)
    * @param dst  待合并的目标区段(起始位置，结束位置)
    * @param desc 降/升序(默认为true,降序)
    * @return
    */
  def merge2Seq2(seq: mutable.Seq[A], src: (Int, Int), dst: (Int, Int), desc: Boolean = true) = HeapSort.merge2Seq2(f)(seq, src, dst, desc)

  /**
    * 对seq中两个已经排序的区段进行合并排序，将src合并到dst<br>
    * 该算法在排序过程不申请新内存
    *
    * @param seq  可变集合
    * @param src  待合并的源区段(起始位置，结束位置)
    * @param dst  待合并的目标区段(起始位置，结束位置)
    * @param desc 降/升序(默认为true,降序)
    * @return
    */
  def merge2SeqNM(seq: mutable.Seq[A], src: (Int, Int), dst: (Int, Int), desc: Boolean = true) = HeapSort.merge2SeqNM(f)(seq, src, dst, desc)

  def mutable_sort_par(seq: mutable.Seq[A], top: Int, desc: Boolean = true) = HeapSort.mutable_sort_par(f)(seq, top, desc)
}

object HeapSort {
  def sort[A, B, S <: Iterable[A]](f: A => B)(iterator: S, top: Int = 0, desc: Boolean = true)(implicit ord: Ordering[B]) = {
    val bf = iterator.toBuffer
    sort_mutable(f)(bf, top, desc)
  }

  def sort_mutable[A, B, S <: mutable.Seq[A]](f: A => B)(seq: S, top: Int = 0, desc: Boolean = true)(implicit ord: Ordering[B]) = {
    sort_mutableRange(f)(seq, top, desc)()
    (if (top < seq.length && top > 0) seq.takeRight(top) else seq).reverse
  }

  private def sort_mutableRange[A, B, S <: mutable.Seq[A]](f: A => B)(seq: S, top: Int = 0, desc: Boolean = true)(from: Int = 0, until: Int = seq.length)(implicit ord: Ordering[B]) = {
    buildHeapRange(f)(seq, desc)(from, until); // 构建堆

    val sublen = until - from
    val toplen = if (top <= 0 || top >= sublen) sublen else top
    var i = until - 1
    var continue = true
    while (continue) {
      swap(seq, from, i)
      if (i > (until - toplen)) {
        heapify(f)(seq, from, i, desc, from)
        i -= 1
      } else continue = false
    }
    (i, until)
  }

  private def buildHeapRange[A, B](f: A => B)(seq: mutable.Seq[A], desc: Boolean)(from: Int, until: Int)(implicit ord: Ordering[B]) = {
    var i = from + ((until - from) >>> 1) - 1
    while (i >= from) {
      heapify(f)(seq, i, until, desc, from)
      i -= 1
    }
  }

  def cmp1_gt[A, B](f: A => B)(l: A, r: A)(implicit ord: Ordering[B]) = ord.gt(f(l), f(r))

  def cmp1_lt[A, B](f: A => B)(l: A, r: A)(implicit ord: Ordering[B]) = ord.lt(f(l), f(r))

  def cmp_gt[A, B](f: A => B, seq: mutable.Seq[A])(l: Int, r: Int)(implicit ord: Ordering[B]) = cmp1_gt(f)(seq(l), seq(r))

  def cmp_lt[A, B](f: A => B, seq: mutable.Seq[A])(l: Int, r: Int)(implicit ord: Ordering[B]) = cmp1_lt(f)(seq(l), seq(r))

  private def heapify[A, B](f: A => B)(seq: mutable.Seq[A], startpos: Int, max: Int, desc: Boolean, off: Int)(implicit ord: Ordering[B]): Unit = {
    def gt = (l: Int, r: Int) => cmp_gt(f, seq)(l, r)

    def lt = (l: Int, r: Int) => cmp_lt(f, seq)(l, r)

    val cmp = if (desc) gt else lt
    var largest = 0
    var idx = startpos
    var right = 0
    var left = 0
    do {
      right = off + ((idx - off + 1) << 1)
      left = right - 1
      largest = if (left < max && cmp(left, idx))
        left
      else
        idx
      if (right < max && cmp(right, largest))
        largest = right
      if (largest != idx) {
        swap(seq, largest, idx)
        idx = largest
      } else return
    } while (true)
  }

  private def swap[A](seq: mutable.Seq[A], i: Int, j: Int) = {
    val temp = seq(i)
    seq(i) = seq(j)
    seq(j) = temp
  }

  private def swap3[A](seq: mutable.Seq[A], i: Int, j: Int, k: Int) = {
    val temp = seq(i)
    seq(i) = seq(j)
    seq(j) = seq(k)
    seq(k) = temp
  }

  //  private def _duplicateSeq[A](src: collection.Seq[A], srcPos: Int, dest: mutable.Seq[A], destPos: Int, length: Int): mutable.Seq[A] = {
  //    for (i <- 0 until length) dest(destPos + i) = src(srcPos + i)
  //    dest
  //  }
  private def _duplicateSeq[A](src: collection.Seq[A], srcPos: Int, dest: mutable.Seq[A], destPos: Int, length: Int): mutable.Seq[A] = {
    var i = 0
    while (i < length) {
      dest(destPos + i) = src(srcPos + i)
      i += 1
    }
    dest
  }

  def merge2Seq[A, B](f: A => B)(seq: mutable.Seq[A], src: (Int, Int), dst: (Int, Int), desc: Boolean)(implicit ord: Ordering[B]): (Int, Int) = {
    if (!(if (desc) cmp_gt(f, seq)(dst._1, src._2 - 1) else cmp_lt(f, seq)(dst._1, src._2 - 1))) {
      if (if (desc) cmp_gt(f, seq)(src._1, dst._2 - 1) else cmp_lt(f, seq)(src._1, dst._2 - 1)) {
        val (srclen, dstlen) = (src._2 - src._1, dst._2 - dst._1)
        val cplen = math.min(srclen, dstlen)
        _duplicateSeq(seq, dst._1 + cplen, seq, dst._1, dstlen - cplen)
        _duplicateSeq(seq, src._2 - cplen, seq, dst._2 - cplen, cplen)
      } else {
        val q = mutable.Queue[A]()

        def gt = (r: Int) => cmp1_gt(f)(seq(r), q.head)

        def lt = (r: Int) => cmp1_lt(f)(seq(r), q.head)

        val cmpdst = if (desc) gt else lt
        var (topsrc, idx) = (src._2 - 1, dst._2 - 1)
        while (idx >= dst._1) {
          q.enqueue(seq(idx))
          if (cmpdst(topsrc)) {
            seq(idx) = seq(topsrc)
            topsrc -= 1
          } else
            seq(idx) = q.dequeue()
          idx -= 1
        }
        while (idx >= dst._1) {
          seq(idx) = q.dequeue()
          idx -= 1
        }
      }
    }
    dst
  }

  def merge2Seq2[A, B](f: A => B)(seq: mutable.Seq[A], src: (Int, Int), dst: (Int, Int), desc: Boolean)(implicit ord: Ordering[B]): (Int, Int) = {
    if (!(if (desc) cmp_gt(f, seq)(dst._1, src._2 - 1) else cmp_lt(f, seq)(dst._1, src._2 - 1))) {
      if (if (desc) cmp_gt(f, seq)(src._1, dst._2 - 1) else cmp_lt(f, seq)(src._1, dst._2 - 1)) {
        val (srclen, dstlen) = (src._2 - src._1, dst._2 - dst._1)
        val cplen = math.min(srclen, dstlen)
        _duplicateSeq(seq, dst._1 + cplen, seq, dst._1, dstlen - cplen)
        _duplicateSeq(seq, src._2 - cplen, seq, dst._2 - cplen, cplen)
      } else {
        val q = seq.slice(dst._1, dst._2)

        def gt = (l: Int, r: Int) => cmp1_gt(f)(seq(l), q(r))

        def lt = (l: Int, r: Int) => cmp1_lt(f)(seq(l), q(r))

        val cmpdst = if (desc) gt else lt
        var (topdst, topsrc, idx) = (q.length - 1, src._2 - 1, dst._2 - 1)
        while (idx >= dst._1 && topsrc >= src._1) {
          if (cmpdst(topsrc, topdst)) {
            seq(idx) = seq(topsrc)
            topsrc -= 1
          } else {
            seq(idx) = q(topdst)
            topdst -= 1
          }
          idx -= 1
        }
        if (idx >= dst._1)
          _duplicateSeq(q, topdst - (idx - dst._1), seq, dst._1, idx - dst._1 + 1)
      }
    }
    dst
  }

  def merge2SeqNM[A, B](f: A => B)(seq: mutable.Seq[A], src: (Int, Int), dst: (Int, Int), desc: Boolean)(implicit ord: Ordering[B]): (Int, Int) = {
    if (!(if (desc) cmp_gt(f, seq)(dst._1, src._2 - 1) else cmp_lt(f, seq)(dst._1, src._2 - 1))) {
      if (if (desc) cmp_gt(f, seq)(src._1, dst._2 - 1) else cmp_lt(f, seq)(src._1, dst._2 - 1)) {
        val (srclen, dstlen) = (src._2 - src._1, dst._2 - dst._1)
        val cplen = math.min(srclen, dstlen)
        _duplicateSeq(seq, dst._1 + cplen, seq, dst._1, dstlen - cplen)
        _duplicateSeq(seq, src._2 - cplen, seq, dst._2 - cplen, cplen)
      } else {
        var (idx, qbf, qbt, qh) = (dst._2 - 1, dst._2 - 1, dst._2 - 1, dst._2 - 1)
        var st = src._2 - 1
        var swapst = () => {}
        var swapqh = () => {}

        def gt = (l: Int) => cmp_gt(f, seq)(l, qh)

        def lt = (l: Int) => cmp_lt(f, seq)(l, qh)

        val cmpdst = if (desc) gt else lt

        def swaptop(top: Int) = {
          val temp = seq(idx)
          seq(idx) = seq(top)
          seq(top) = temp
        }

        def getql = () => qbf + (qh - qbf + 1) % (qbt - qbf + 1)

        def nextqh = () => qbt - (qbt - qh + 1) % (qbt - qbf + 1)

        //      def moveStep(from: Int, to: Int, step: Int) =for (i <- (if (step > 0) (from to to).reverse else (from to to))) seq(i + step) = seq(i)
        def moveStep(from: Int, to: Int, step: Int) = {
          var i = if (step > 0) to else from

          def upf() = i >= from

          def dnt() = i <= to

          val (s, c) = if (step > 0) (-1, upf _) else (1, dnt _)
          while (c()) {
            seq(i + step) = seq(i)
            i += s
          }
        }

        def swapLeft(from: Int, to: Int) = {
          val tmp = seq(from - 1)
          moveStep(from, to, -1)
          seq(to) = tmp
        }

        def swapRight(from: Int, to: Int) = {
          val tmp = seq(to + 1)
          moveStep(from, to, 1)
          seq(from) = tmp
        }

        def swapStTail() = {
          swaptop(st)
          val ql = getql()
          if (ql > qbf)
            if (qh - qbf > qbt - ql) {
              swap(seq, st, qbt)
              swapRight(ql, qbt - 1)
              qbf = st
            } else {
              swapLeft(qbf, qh)
              qbf = st
              qh = nextqh()
            }
          else {
            qbf = st
          }
        }

        def swapStHead() = {
          swaptop(st)
          swapst = swapStTail
          swapqh = swapQhEnable
          qh = st
          qbf = st
          qbt = st
        }

        def swapQhDisable() = {
          qbf -= 1
          qbt -= 1
          qh -= 1
        }

        def swapQhEnable() = {
          swaptop(qh)
          qh = nextqh()
        }

        swapst = swapStHead
        swapqh = swapQhDisable
        while (idx >= dst._1 && st >= src._1) {
          if (cmpdst(st)) {
            swapst()
            st -= 1
          } else
            swapqh()
          idx -= 1
        }
        if (idx >= dst._1) {
          val ql = getql()
          _duplicateSeq(seq, ql, seq, dst._1, qbt - ql + 1)
          _duplicateSeq(seq, qbf, seq, dst._1 + qbt - ql + 1, ql - qbf)
        }
      }
    }
    dst
  }

  private val processors = Runtime.getRuntime.availableProcessors()

  //获取cpu核心数
  def top_mutable_par[A, B, M <: mutable.Seq[A]](f: A => B)(seq: M, top: Int, desc: Boolean = true)(implicit ord: Ordering[B]) = {
    //根据cpu核心数对要排序的数据分段
    val step = (seq.length + processors - 1) / processors
    //以并行方式对每一段数据进行排序
    val rangs = for (i <- (0 until (seq.length + step - 1) / step).par) yield {
      sort_mutableRange(f)(seq, top)(i * step, math.min(seq.length, (i + 1) * step))
    }

    def merge = (left: (Int, Int), right: (Int, Int)) =>
      if ((right._2 - right._1) > (left._2 - left._1))
        merge2SeqNM(f)(seq, left, right, desc)
      else
        merge2SeqNM(f)(seq, right, left, desc)

    //调用用reduce对分段排序后的结果进行合并
    val r = rangs.reduce(merge(_, _))
    //返回排序结果(需要反序)
    seq.slice(r._1, r._2).reverse
  }

  def mutable_sort_par[A, B, M <: mutable.Seq[A]](f: A => B)(seq: M, top: Int, desc: Boolean = true)(implicit ord: Ordering[B]) = {
    //根据cpu核心数对要排序的数据分段
    val step = (seq.length + processors - 1) / processors
    //以并行方式对每一段数据进行排序
    val rangs = for (i <- (0 until (seq.length + step - 1) / step).par) yield {
      sort_mutableRange(f)(seq, top)(i * step, math.min(seq.length, (i + 1) * step))
    }

    def merge = (le: (Int, Int), ri: (Int, Int)) => {

      val lo = le._1
      val mid = (le._2 + ri._1) / 2
      val hi = ri._2 - 1

      var i = lo
      var j = mid + 1
      //      val left = seq.slice(le._1, le._2)
      //      val right = seq.slice(ri._1, ri._2)
      val aux = seq.clone()

      for (k <- lo to hi) {
        if (i > mid) {
          seq(k) = aux(j)
          j += 1
        } else if (j > hi) {
          seq(k) = aux(i)
          i += 1
        } else if (aux(i).toString.toInt < aux(j).toString.toInt) {
          seq(k) = aux(i)
          i += 1
        } else {
          seq(k) = aux(j)
          j += 1
        }
      }
      ri
    }

    rangs.reduce(merge(_, _))
    seq
  }


  def main(args: Array[String]) {
    //测试代码
    val st1 = System.currentTimeMillis()
    val m = new HeapSort[Int, Int, mutable.Buffer[Int]]((w: Int) => w)
    val rnd = new java.util.Random()
    val l = new Array[Int](100)
    for (i <- 0 until 5) {
      l(i) = rnd.nextInt(100)
    }
    for (i <- 5 until l.length) {
      l(i) = rnd.nextInt(100)
    }
    val st2 = System.currentTimeMillis()
    printf("create new array cost:%f seconds(%d millis) used\n", (st2 - st1) / 1024D, st2 - st1)
    for (i <- 0 to 0) {
      println("==============time ", i, "=================")
      val s = l.toBuffer[Int]
      println(s)
      //      val t1 = System.currentTimeMillis
      //      val r1 = m.sort_range(s, 0)(0, 5)
      //      val r2 = m.sort_range(s, 0)(5, 10)
      //      val t2 = System.currentTimeMillis
      //      printf("sort time cost:%f seconds(%d mills) used\n", (t2 - t1) / 1024D, t2 - t1)
      //      for (i <- r1._1 until r1._2) {
      //        print(s(i) + ",")
      //      }
      //      println(r1)
      //
      //      for (i <- r2._1 until r2._2) {
      //        print(s(i) + ",")
      //      }
      //      println(r2)
      //
      //      m.merge2Seq2(s, r1, r2)
      //      for (i <- (r2._1 until r2._2).reverse) {
      //        print(s(i) + ",")
      //      }
      //      println(r2)

      //      val arr = Array(1, 5, 9, 10, 12, 3, 5, 8, 9, 11).toBuffer
      //      val r1 = m.sort_range(arr, 0)(0, 4)
      //      val r2 = m.sort_range(arr, 0)(5, 10)
      //      m.merge2Seq(arr, r1, r2)
      //      for (i <- (r2._1 until r2._2).reverse) {
      //        print(arr(i) + ",")
      //      }
      val p1 = System.currentTimeMillis()
      val r3 = m.mutable_sort_par(s, 0)
      val p2 = System.currentTimeMillis()
      printf("parallel sored time cost:%f seconds(%d millis) used\n", (p2 - p1) / 1024D, p2 - p1)
      println(r3)
    }
  }
}

