{:db {:datomic-client {:server-type :dev-local
                       :system "dev"}}
 :http {:port 8080
        :base-path "testapp"}
 :worker {:threads-n 10
          :chan-buffer-n 10
          :real-threads false}}
