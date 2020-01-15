(ns mpd.time-format-test
  (:require [clojure.test :refer [deftest is testing]]
            [mpd.time-format :refer [encode decode]]))

(deftest a-test
  (testing "decode"
    (is (= (decode "1")
           60))

    (is (= (decode "3")
           (* 60 3)))

    (is (= (decode "1s")
           1))

    (is (= (decode "4s")
           4))

    (is (= (decode "1m")
           60))

    (is (= (decode "5m")
           (* 5 60)))

    (is (= (decode "1h")
           (* 1 60 60)))

    (is (= (decode "8d")
           (* 8 24 60 60)))

    (is (= (decode "1h2m3s4d")
           (+ (* 1 60 60)
              (* 2 60)
              3
              (* 4 24 60 60))))

    (is (= (decode "1h3s")
           (+ (* 1 60 60)
              3)))

    (is (= (decode "2m3s")
           (+ (* 2 60)
              3)))

    (is (= (decode "6h7")
           (+ (* 6 60 60)
              (* 7 60))))

    (is (= (decode "  1h   2m   3s   ")
           (+ (* 1 60 60)
              (* 2 60)
              3))))

  (testing "encode"
    (is (= (encode (decode "1h3m"))
           "1h3m"))

    (is (= (encode (decode "2h3m4s"))
           "2h3m4s"))

    (is (= (encode (decode "2h3m4s8d"))
           "8d2h3m4s"))

    (is (= (encode (decode "30"))
           "30m"))))
