(ns blockchain.sha256
  (:import [java.security MessageDigest]
           [javax.xml.bind DatatypeConverter]))

(defn- sha256-digest
  [bs]
  (doto (MessageDigest/getInstance "SHA-256") (.update bs)))

(defn sha256
  [msg]
  (-> msg
      .getBytes
      sha256-digest
      .digest
      DatatypeConverter/printHexBinary))

