(ns lamb.imports)


(defprotocol WritableClojure
  (read-writable [this] "read Writable objects form Hadoop"))

(defn import-writables
  []
  (import '(org.apache.hadoop.io
            BooleanWritable BytesWritable ByteWritable
            DoubleWritable FloatWritable IntWritable LongWritable
            MapWritable NullWritable ObjectWritable ;SortedMapWritable
            Text VLongWritable))
  (extend-protocol WritableClojure
    org.apache.hadoop.io.BooleanWritable
    (read-writable [src] (.get src))

    org.apache.hadoop.io.BytesWritable
    (read-writable [src] (.getBytes src))

    org.apache.hadoop.io.ByteWritable
    (read-writable [src] (.get src))

    org.apache.hadoop.io.DoubleWritable
    (read-writable [src] (.get src))

    org.apache.hadoop.io.FloatWritable
    (read-writable [src] (.get src))

    org.apache.hadoop.io.IntWritable
    (read-writable [src] (.get src))

    org.apache.hadoop.io.LongWritable
    (read-writable [src] (.get src))

    org.apache.hadoop.io.MapWritable
    (read-writable [src]
      (into {}
            (for [[k v] src]
              [(read-writable k) (read-writable v)])))

    org.apache.hadoop.io.NullWritable
    (read-writable [src] nil)

    org.apache.hadoop.io.ObjectWritable
    (read-writable [src] ^{:tab (.getDeclaredClass)} (.get src))

    org.apache.hadoop.io.Text
    (read-writable [src] (.toString src))

    org.apache.hadoop.io.VLongWritable
    (read-writable [src] (.get src))))

