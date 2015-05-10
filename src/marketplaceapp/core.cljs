(ns ^:figwheel-always marketplaceapp.core
    (:require [rum :as rum :include-macros true]
            [dommy.core :refer-macros [sel sel1]]))

(enable-console-print!)

(defn row-data
  []
  {"temperature" (rand-int 100) "humidity" (rand-int 100)})

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
    :rows (into []
            (take 10 (cycle [(row-data)
                             (row-data) 
                             (row-data)])))}
  )

(defonce app-state
  (atom initial-state))

(defn reset-state! []
  (reset! app-state initial-state))

(defn header-row
    [value subtitle]
    [:th.column_head.scroll_columns value [:span.subtitle subtitle]])

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
    [:img.pix {:height "auto" :src "data:image/jpeg;base64,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"}]
    [:table.table.table-striped.sortable
        [:thead
         [:tr (for [col columns]
                (header-row (get col "title") (get col "subtitle")))
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
