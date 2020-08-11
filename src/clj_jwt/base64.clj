(ns clj-jwt.base64
  (:require [clojure.data.codec.base64 :as base64]
            [clojure.string            :as str])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(defprotocol ByteArrayInput (input-stream [this]))

(extend-type String
  ByteArrayInput
  (input-stream [src] (ByteArrayInputStream. (.getBytes src "UTF-8"))))

(extend-type (Class/forName "[B")
  ByteArrayInput
  (input-stream [src] (ByteArrayInputStream. src)))

;; Encoder
(defn encode
  ^bytes
  [x]
  (with-open [^ByteArrayInputStream in (input-stream x)
              out (ByteArrayOutputStream.)]
    (base64/encoding-transfer in out)
    (.toByteArray out)))

(defn encode-str
  [x & {:keys [^String charset] :or {charset "UTF-8"}}]
  (String. (encode x) charset))

;; Decoder
(defn decode
  ^bytes
  [x]
  (with-open [^ByteArrayInputStream in (input-stream x)
              out (ByteArrayOutputStream.)]
    (base64/decoding-transfer in out)
    (.toByteArray out)))

(defn decode-str
  [x & {:keys [^String charset] :or {charset "UTF-8"}}]
  (String. (decode x) charset))

;; URL-Safe Encoder
(defn url-safe-encode-str
  [x]
  (-> (encode-str x)
      (str/replace #"\s" "")
      (str/replace "=" "")
      (str/replace "+" "-")
      (str/replace "/" "_")))

;; URL-Safe Decoder
(defn url-safe-decode
  ^bytes
  [^String s]
  (-> (condp = (mod (.length s) 4)
        2 (str s "==")
        3 (str s "=")
        s)
      (str/replace "-" "+")
      (str/replace "_" "/")
      decode))

(defn url-safe-decode-str
  [^String s & {:keys [^String charset] :or {charset "UTF-8"}}]
  (String. (url-safe-decode s) charset))
