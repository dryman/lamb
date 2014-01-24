(ns lamb.core
  (:import
   [org.apache.hadoop.mapreduce Mapper Reducer Job]
   [org.apache.hadoop.fs Path]
   [org.apache.hadoop.util Tool ToolRunner]
   [org.apache.hadoop.conf Configuration Configured]
   [org.apache.hadoop.mapreduce.lib.reduce IntSumReducer LongSumReducer]
   [org.apache.hadoop.mapreduce.lib.output FileOutputFormat]
   [org.apache.hadoop.mapreduce.lib.input MultipleInputs TextInputFormat])
  (:use lamb.imports)
  (:gen-class
   :extends org.apache.hadoop.conf.Configured
   :implements [org.apache.hadoop.util.Tool]
   :init init
   :state state))

(import-writables)

(defn -init [] [[] {}])



(defmacro defmapper [name [key-in val-in context] & body]
  `(do
     (gen-class
      :name ~(symbol (str *ns* '. name))
      :prefix ~(symbol (str name '-))
      :extends org.apache.hadoop.mapreduce.Mapper
      )
     (defn
       ~(symbol (str name "-map"))
       [this# key# val# ~context]
       (let [~key-in (read-writable key#)
             ~val-in (read-writable val#)]
         ~@body))))



;; (gen-mapper-class General
;;   :context-key (Text.)
;;   :context-val (IntWritable.)
;;   :body (fn [_ {:keys [ct_action ct_audit]}]
;;           (if (= ct_audit "0")
;;             [ct_action 1])))

;; (defmapper Test [_ {:keys [ct_audit]}]
;;   (if (= ct_audit "0")
;;     [ct_audit 1 "total" 1]))



(defmapper Map [key val context]
  (.write context
          (LongWritable. (count (clojure.string/split val #"\t")))
          (LongWritable. 1)))



(defmapper ActionCount [key val context]
  (let [[audit timestamp action & fields] (clojure.string/split val #"\t")]
    (.write context (Text. action) (LongWritable. 1))))


(defn -main [& argv]
  (ToolRunner/run (Configuration.) (lamb.core.) (into-array String argv)))

(defn -run [this args]
  (let [job (doto (Job. (.getConf this))
              (.setJobName "Hadoop lamb test")
              (.setJarByClass lamb.core)
              (.setMapOutputKeyClass LongWritable)
              (.setMapOutputValueClass LongWritable)
              (.setReducerClass LongSumReducer)
              (.setNumReduceTasks 1))
        [src dst] args]
    (MultipleInputs/addInputPath job (Path. src) TextInputFormat lamb.core.Map)
    (FileOutputFormat/setOutputPath job (Path. dst))
    (.submit job)
    (.waitForCompletion job true)
    0))
