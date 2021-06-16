(ns hello
  (:require [cognitect.transit :as transit])
  (:import [com.turtlequeue Client Consumer Producer Topic Message]
           [java.util.function Function]))



(defn api-key []
  (if-let [s (System/getenv "TURTLEQUEUE_API_KEY")]
    s
    (println "MISSING_API_KEY! Make sure to get it in the dashboard at https://turtlequeue.com and export it!")))

(defn user-token []
  (if-let [s (System/getenv "TURTLEQUEUE_USER_TOKEN")]
    s
    (println "MISSING_USER_TOKEN! Make sure to get it in the dashboard at https://turtlequeue.com and export it!")))

(defn client []
  (-> (Client/builder)
      (.setUserToken (user-token))
      (.setApiKey (api-key))
      ;; add transit for clj types
      (.transitReader (reify
                        java.util.function.Function
                        (apply [this in]
                          (.r (transit/reader in :json)))))
      (.transitWriter (reify
                        java.util.function.Function
                        (apply [this out]
                          (.w (transit/writer out :json)))))
      (.dataFormat "application/transit+json")
      (.build)
      (.connect)
      (.get)))

(defn topic [{:keys [topic namespace persistent?]}]
  (-> (Topic/builder)
      (.topic topic)
      (.namespace namespace)
      (.persistent persistent?)
      (.build)))

(defn consumer [^Client c ^Topic t]
  (-> (.newConsumer c)
      (.topic t)
      (.subscriptionName "helloClojure")
      (.subscribe)
      (.get)))

(defn producer [^Client c ^Topic t]
  (-> (.newProducer c)
      (.topic t)
      (.create)
      (.get)))

(defn reader [^Client c ^Topic t]
  (-> (.newReader c)
      (.topic t)
      (.create)
      (.get)))

(defn message [^Producer p x]
  (-> (.newMessage p)
      (.value x)
      (.send)))

(defn reader-seq
  "takes a TurtleQueue Reader and returns a lazy-seq that stops when no more messages are available"
  ([r]
   (if (.get (.hasMessageAvailable r))
     (lazy-seq (reader-seq r (.get (.readNext r))))
     (lazy-seq nil)))
  ([r el]
   (if (.get (.hasMessageAvailable r))
     (lazy-seq (cons el (reader-seq r (.get (.readNext r)))))
     (lazy-seq (cons el nil)))))

(def my-topic (topic {:namespace "default"
                      :persistent? true
                      :topic "helloClojureTestTopic"}))

(defn run
  "Very simple example about running TurtleQueue
  1. create a consumer and a producer on a topic
  2. publish 10 messages
  3. logs the messages being received"
  [this]
  (println "Starting TQ")

  (when-not (and (api-key)
                 (user-token))
    (System/exit 1))

  ;; 1. setup the connection/consumer/producer
  (with-open [my-client (client)
              my-consumer (consumer my-client my-topic)
              my-producer (producer my-client my-topic)
              my-reader (reader my-client my-topic)]
    (println "Connected!")

    ;; 2. use the producer to send 10 messages
    (doseq [n (range 10)]
      (println "Sending: " {:message n})
      (message my-producer {:message n}))

    ;; 3. read them from the corresponding  consumer
    (doseq [_n (range 10)]
      (let [^Message msg (-> (.receive my-consumer)
                             (.get))]
        (println "Received: " (.getData msg))))

    ;; 4. re-read them using a reader
    (->> (reader-seq my-reader)
         (map (fn [msg] (println "Reading: " (.getData msg))))
         (doall))

    ))

(defn -main []
  (run nil))
