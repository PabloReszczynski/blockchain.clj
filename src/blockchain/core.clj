(ns blockchain.core
  (:require [blockchain.sha256 :as sha256]
            [clojure.string :as str]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]))

(defn Block
  [index previous-hash timestamp data current-hash]
  {:index index
   :previous-hash previous-hash
   :timestamp timestamp
   :data data
   :current-hash current-hash})

(defn calculate-hash
  [index previous-hash timestamp data]
  (let [value (str/join [(str index)
                         (str previous-hash)
                         (str timestamp)
                         (str data)])
        sha (sha256/sha256 value)]
    (str sha)))

(defn calculate-hash-for-block
  [block]
  (calculate-hash
   (:index block)
   (:previous-hash block)
   (:timestamp block)
   (:data block)))

(defn get-latest-block
  [blockchain]
  (last blockchain))

(defn generate-next-block
  [blockchain blockdata]
  (let [previous-block (get-latest-block blockchain)
        next-index (+ 1 (:index previous-block))
        previous-hash (:current-hash previous-block)
        next-timestamp (str (tc/to-long (time/now)))
        next-hash (calculate-hash next-index
                                  previous-hash
                                  next-timestamp
                                  blockdata)]
    (concat blockchain [(Block next-index
                               previous-hash
                               next-timestamp
                               blockdata
                               next-hash)])))

(defn =block?
  [block1 block2]
  (every? #(= (first %) (last %))
          (map #(map % [block1 block2])
               [:index :previous-hash :timestamp :data :hash])))

(defn is-valid-new-block?
  [new-block previous-block]
  (every? #(= (first %) (last %))
          [[(+ 1 (:index previous-block)) (:index new-block)]]
          [[(:hash previous-block) (:previous-hash new-block)]]
          [[(calculate-hash-for-block new-block) (:hash new-block)]]))

(defn is-valid-chain?
  [blockchain]
  (reduce is-valid-new-block? genesis blockchain))

(def genesis
  (Block 0 "0" "1496518102.896031" "The very first block" "0q23nfa0se8fhPH234hnjldapjfasdfansdf23"))

(def second-block
  (generate-next-block [genesis] "The second block!"))

(println (last second-block))
