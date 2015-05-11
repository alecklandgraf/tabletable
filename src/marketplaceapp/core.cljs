(ns ^:figwheel-always marketplaceapp.core
    (:require [rum :as rum :include-macros true]
            [dommy.core :refer-macros [sel sel1]]))

(enable-console-print!)

(defn data-generate-row-data
  "generates a row's data"
  []
  {"temperature" (rand-int 100) "humidity" (rand-int 100)})
(defn data-generate
  "generates n number of a row's data"
  [n]
  (for [_ (range n)] (data-generate-row-data)))
(defn sort-data
  "func to demonstrate how passing a callback might work"
  [sort-column-name]
  (prn sort-column-name))

(def initial-state
{
    :counter 0
    :columns [{"sort_column" "temperature"
               "title" "Temperature"}
              {"sort_column" "humidity"
               "title" "Humidity"
               "unit" "%"
               "subtitle" "%"}
               ]
    :rows (data-generate 10)})

(defonce app-state
  (atom initial-state))

(defn reset-state! []
  (reset! app-state initial-state))

(defn header-row
    [value subtitle click-callback]
    (defn handle-click
      []
      (click-callback value))
    [:th.column_head.scroll_columns {:on-click handle-click} value [:span.subtitle subtitle]])

(defn row-td
    [value]
    [:td.scroll_columns.is_aligned_left value])

(defn render-row
  "renders a tr"
  [row columns]
  [:tr
    (for [col columns]
      (let [sort-column (get col "sort_column")
            row-value (get row sort-column)
            unit (get col "unit" "")
            row-text (str row-value unit)]
        (row-td row-text)))])



(rum/defc be-table
  "renders the be-table"
  [columns rows]
  [:div.vert_table_scroll_container
    [:table.table.table-striped.sortable
        [:thead
         [:tr (for [col columns]
                (header-row (get col "title") (get col "subtitle") sort-data))
         ]]
        [:tbody
         (for [row rows]
           (render-row row columns))]
     ]
   ])

(rum/defc app-ui [app]
  [:div.app
   (be-table (:columns app) (:rows app))
   [:h2 {:on-click (fn [& _] (swap! app-state update :counter inc))}
    "App UI"]
   [:ul
    (for [i (range (:counter app))]
      [:li {:key i} (str "list item #" i)])]])

(rum/defc app-container < rum/reactive
  [app-atom]
  (app-ui (rum/react app-atom)))

(defn main []
  ;; the @app-state is sugar for (deref app-state) which returns the
  ;; current value of the app-state atom (which is mutable using the swap! function)
  (rum/mount
   ;(app-ui @app-state)  ; instantiating our app-ui component
   (app-container app-state)
   (sel1 :#app)))       ; selecting a dom node to mount


;; this will re-render the ui if this file is reloaded
(main)

;; uncomment to reset state on file reload
(reset-state!)
