(defproject lamb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main lamb.core
  :aot [lamb.core]
  :profiles {:provided
             {:dependencies
              [[org.apache.hadoop/hadoop-core "2.0.0-mr1-cdh4.2.0"]
               [org.apache.hadoop/hadoop-common "2.0.0-cdh4.2.0"]
               ;[org.clojure/clojure "1.5.1"]
               ]
              }}
  :repositories [[ "cloudera"                 "https://repository.cloudera.com/artifactory/cloudera-repos/"]])
