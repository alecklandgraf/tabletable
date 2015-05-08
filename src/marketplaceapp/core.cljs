(ns ^:figwheel-always marketplaceapp.core
    (:require [rum :as rum :include-macros true]
            [dommy.core :refer-macros [sel sel1]]))

(enable-console-print!)
(def initial-state
  {
    :counter 0
    :columns [{"sort_column" "temperature"
               "title" "Temperature"}
              {"sort_column" "humidity"
               "title" "Humidity"
               "unit" "%"}]
    :rows [{"temperature" 88 "humidity" 44}
           {"temperature" 55 "humidity" 33}
           {"temperature" 110 "humidity" 66}]
  })

(defonce app-state
  (atom initial-state))

(defn reset-state! []
  (reset! app-state initial-state))


(defn header-row
    [value]
    [:th.column_head.scroll_columns value])
(defn row-td
    [value]
    [:td.scroll_columns.is_aligned_left value])

(rum/defc be-table [columns rows]
  [:div.vert_table_scroll_container
    [:table.table.table-striped.sortable
        [:thead
         [:tr (for [col columns]
                (header-row (get col "title")))
         ]]
        [:tbody
         (for [row rows]
           (for [col columns]
             (row-td (get row (get col "sort_column")))
             )
           )
         ;[:tr (row-td 77.3) (row-td "35%")]
         ]
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
;; (reset-state!)
