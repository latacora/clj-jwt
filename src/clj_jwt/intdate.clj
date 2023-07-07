(ns clj-jwt.intdate
  (:require
    [clj-time.coerce :refer [to-long from-long]]))



(defn- joda-time? [x] (= org.joda.time.DateTime (type x)))




(defn joda-time->intdate
  [d]
  {:pre [(joda-time? d)]}
  (long (/ (to-long d) 1000)))


(defn intdate->joda-time
  [i]
  {:pre [(integer? i) (pos? i)]}
  (from-long (* i 1000)))


(defn ensure-intdate [x]
  (cond
    (int? x)
    x
    (joda-time? x)
    (joda-time->intdate x)))
