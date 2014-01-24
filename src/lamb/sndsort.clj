(ns lamb.sndsort)


(gen-class
 :name lamb.core.SndKey
 :implements [org.apache.hadoop.io.WritableComparable]
 :state state
 :init init
 :constructors {[clojure.lang.IPersistentMap] []
                [String Long] []
                [] []})

(defn -init
  ( []             [[] (atom {})])
  ( [map]          [[] (atom map)])
  ( [content mark] [[] (atom {:content content
                              :mark    mark})]))

(defn -readFields
 [this in]
 (swap! (.state this) assoc :content (read-string (.readUTF in)))
 (swap! (.state this) assoc :mark (read-string (.readLong in))))

; need to use pr_str to put clojure obj into string representation

(defn -write
 [this out]
 (.writeUTF out @(:content (.state this)))
 (.writeLong out @(:mark (.state this))))

(defn -compareTo
 [this that]
 (let [this-content (-> this .state deref :content str)
       this-mark    (-> this .state deref :mark)
       that-content (-> that .state deref :content str)
       that-mark    (-> that .state deref :mark)]
   (if-let [cmp (compare this-content that-content)]
     cmp
     (compare this-mark that-mark))))

(defn -toString
  [this]
  (str @(.state this)))


(deftype SndsortKey [^{:volatile-mutable true} content
                     ^{:volatile-mutable true} mark]
  org.apache.hadoop.io.WritableComparable

  (readFields [this in]
    (set! content (read-string (.readUTF in)))
    (set! mark (read-string (.readInt in))))

  (write [this out]
    (.writeUTF out (str content))
    (.writeInt out mark))

  (compareTo [this that]
    (if-let [cmp (compare (str content) (str (.content that)))]
      cmp
      (compare mark (.mark that)))))
