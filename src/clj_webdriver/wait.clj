(ns clj-webdriver.wait
  (:import clj_webdriver.core.Driver
           org.openqa.selenium.WebDriver))

;;; ## Wait Functionality ##
(defprotocol IWait
  "Implicit and explicit waiting"
  (implicit-wait [driver timeout] "Specify the amount of time the `driver` should wait when searching for an element if it is not immediately present. This setting holds for the lifetime of the driver across all requests. Units in milliseconds.")
  (wait-until
    [driver pred]
    [driver pred timeout]
    [driver pred timeout interval] "Set an explicit wait time `timeout` for a particular condition `pred`. Optionally set an `interval` for testing the given predicate. All units in milliseconds"))

(extend-type Driver
    
  IWait
  (implicit-wait [driver timeout]
    (.implicitlyWait (.. (:webdriver driver) manage timeouts) timeout TimeUnit/MILLISECONDS)
    driver)

  (wait-until [driver pred]
    (wait-until driver pred 5000 0))
  (wait-until [driver pred timeout]
    (wait-until driver pred timeout 0))
  (wait-until [driver pred timeout interval]
    (let [wait (WebDriverWait. (:webdriver driver) (/ timeout 1000) interval)]
      (.until wait (proxy [ExpectedCondition] []
                     (apply [d] (pred d))))
      driver)))

(extend-type WebDriver

  IWait
  (implicit-wait [driver timeout]
    (.implicitlyWait (.. driver manage timeouts) timeout TimeUnit/MILLISECONDS)
    driver)

  (wait-until
    ([driver pred] (wait-until driver pred 5000 0))
    ([driver pred timeout] (wait-until driver pred timeout 0))
    ([driver pred timeout interval]
       (let [wait (WebDriverWait. driver (/ timeout 1000) interval)]
         (.until wait (proxy [ExpectedCondition] []
                        (apply [d] (pred d))))
         driver))))