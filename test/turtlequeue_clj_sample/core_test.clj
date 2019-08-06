(ns turtlequeue-clj-sample.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [turtlequeue.api]
            [turtlequeue.reader])
  (:import [java.util.concurrent CompletableFuture]))

(defn- get-token []
  (java.util.UUID/fromString
    (System/getenv "TURTLEQUEUE_USER_TOKEN")))

(defn get-api-key []
  (java.util.UUID/fromString
    (System/getenv "TURTLEQUEUE_API_KEY")))


(deftest turtlequeue-test

  (testing "turtlequeue is awesome"
    (is (true? turtlequeue.api/awesome)))

  (testing "has a version"
    (is turtlequeue.api/version)))


(deftest turtlequeue-api-test

  (testing "can init"
    (is (turtlequeue.api/init {:host (or (System/getenv "TURTLEQUEUE_API_HOST") "turtlequeue.com")
                         :type :ws
                         :protocol :https})))

  (testing "can connect"
    (let [connect-p (promise)]

      (turtlequeue.api/on "connect"
                    (fn [evt]
                      (println "connect event")
                      (deliver connect-p evt)))

      (turtlequeue.api/connect {:UserToken (get-token)
                          :ApiKey (get-api-key)})

      (is (not= ::timeout (deref connect-p 1000 ::timeout)))
      (when (realized? connect-p)
        (is (= (select-keys (second @connect-p) [:auth])
               {:auth {:status :authenticated}})))))


  (testing "can pubsub"
    (let [payload {:msg "hello"
                   :d (java.util.Date.)}
          channel (str "#test-" (System/currentTimeMillis))
          sub-p (promise)
          pub-p (promise)
          ^CompletableFuture sub-res (turtlequeue.api/subscribe {:channel channel}
                                       (fn [err data metadata]
                                         (println "data received on channel #test" err data metadata)
                                         (deliver sub-p {:err err :data data :metadata metadata})))]

      (is (not= ::timeout (deref sub-res 1000 ::timeout)))

      (when (.isDone sub-res)
        (let [^CompletableFuture publish-res (turtlequeue.api/publish {:channel channel
                                                                 :payload payload}
                                                                (fn publish-callback [err data metadata]
                                                                  (deliver pub-p {:err err :data data :metadata metadata})))]

          (is (not= ::timeout (deref publish-res 1000 ::timeout)))
          (is (not= ::timeout (deref sub-p 1000 ::timeout)))

          (is (= (:data (deref publish-res 0 ::timeout))
                 (:data (deref pub-p 0 ::timeout))))

          (when (realized? sub-p)
            (is (nil? (:err @sub-p)))
            (is (= payload (:data @sub-p))))

          (is (not= ::timeout (deref pub-p 1000 ::timeout)))
          (when (realized? pub-p)
            (is (nil? (:err @pub-p)))
            (is (= 1 (:publishedCount (:data @pub-p)))))))))

  )
