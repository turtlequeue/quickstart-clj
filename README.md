<p align="center">
  <img alt="turtlequeue-logo" src="https://turtlequeue.com/logo_black.png" width="230">
</p>

<h3 align="center">TurtleQueue</h3>

<p align="center">
  A Clojure demo using TurtleQueue: hosted publish-subscribe replayable events
</p>

<p align="center">
  <a href="https://circleci.com/gh/turtlequeue/quickstart-clj/tree/master">
    <img alt="CI Status" src="https://circleci.com/gh/turtlequeue/quickstart-clj.svg?style=svg">
  </a>

  <!-- <a href="http://cljdoc.org/d/com.turtlequeue/clj-client"> -->
  <!--   <img alt="cljdoc link" src="https://cljdoc.org/badge/com.turtlequeue/clj-client"> -->
  <!-- </a> -->
</p>

# TurtleQueue Clojure quickstart

How to TurtleQueue in Clojure

## Usage

* Export you Api Key, and User Token, found in https://turtlequeue.com/dashboard/security.html

`export TURTLEQUEUE_USER_TOKEN="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"`

`export TURTLEQUEUE_API_KEY="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"`

`lein repl`

You should be able to publish/subscribe from the REPL, and to run the tests with `lein test`

## License

Copyright © 2019 Turtlequeue Ltd

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
