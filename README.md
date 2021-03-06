# The Missing Clojure Intro (Windows) [originally on dev.to](https://dev.to/adasomg/10-life-changing-minutes-with-clojure-windows-3ofl)
Clojure promises __unprecedented productivity__. Its devs boast __top salaries__.  
It's true! But before you ever get to that point you'll face __unprecedented confusion__.  
Understanding Clojure's tooling __is very challenging__.  

Most guides push you straight into writing Clojure and don't bother explaining its __complex foundations__.

This is different.  
I will __NEVER__ tell you to go do something yourself.
You'll be able to complete this guide __100% uninterrupted__ on any recent Windows machine.
__ZERO knowledge required!__ Nothing will be installed permanently!

We'll get java, discuss java stuff (JARs, the classpath, Maven), play around in Clojure's REPL, install lein, explain what lein really does, and make a simple Clojure program that counts most frequent words in [dev.to](https://dev.to) headlines.

Follow along carefully, don't skip any parts, __faithfully execute every command as I do__!  
Copy-and-paste PowerShell commands<sup id="a1">[1](#f1)</sup>, but when evaluating Clojure __please retype yourself!__  
In __only 10 minutes__ you'll be spared __weeks of frustration__.

Open up a PowerShell (press `Windows key`, type `powershell` then press `Enter`). Please, __don't close it till the end__.  
Ok, let's get started!

# What is Clojure
[Clojure's homepage](https://clojure.org/index) has this to say:

> Clojure is a dynamic, general-purpose programming language, combining the approachability and interactive     development of a scripting language with an efficient and robust infrastructure for multithreaded programming. Clojure is a compiled language, yet remains completely dynamic – every feature supported by Clojure is supported at runtime. Clojure provides easy access to the Java frameworks, with optional type hints and type inference, to ensure that calls to Java can avoid reflection.
<br/><br/>
Clojure is a dialect of Lisp, and shares with Lisp the code-as-data philosophy and a powerful macro system. Clojure is predominantly a functional programming language, and features a rich set of immutable, persistent data structures. When mutable state is needed, Clojure offers a software transactional memory system and reactive Agent system that ensure clean, correct, multithreaded designs.
<br/><br/>
I hope you find Clojure's combination of facilities elegant, powerful, practical and fun to use.
<br/><br/>
Rich Hickey <br/>
author of Clojure and CTO Cognitect

But what is it __really__?  
On the most fundamental level Clojure is just __a Java program__.  
Before we begin we need to get `java`.  

# Getting Java

We will get ourselves an OpenJDK implementation of Java 11.

Even if you already have `java`, please follow along. Not going to affect your existing installation.

In PowerShell:
```powershell
PS C:\Windows\system32> cd ~

PS C:\Users\adas> mkdir clojure

PS C:\Users\adas> cd clojure

# 187MB download
PS C:\Users\adas\clojure> wget https://download.java.net/java/GA/jdk11/13/GPL/openjdk-11.0.1_windows-x64_bin.zip -OutFile java11.zip
# might take a minute or two to download. OpenJDK is almost exactly like Oracle's JDK, 
# for our purposes there's no real difference.
# The JDK (Java Development Kit) includes all the necessary binaries to work with java, 
# like java, javac, javah, javap and so on...
Writing web request ...
....

# load up .NET stuff for extracting Zips into PowerShell (no need to understand)
PS C:\Users\adas\clojure> Add-Type -AssemblyName System.IO.Compression.FileSystem 
# we extract the zip we just downloaded
PS C:\Users\adas\clojure> [System.IO.Compression.ZipFile]::ExtractToDirectory((Join-Path $PWD "java11.zip"),$PWD) 

# rename the extracted directory to java11
PS C:\Users\adas\clojure> mv jdk-11.0.1 java11 

# we don't need the zip anymore
PS C:\Users\adas\clojure> rm java11.zip 

PS C:\Users\adas\clojure> java11\bin\java -version # make sure the binary works
openjdk version "11.0.1" 318-1-16 # it does
...

# temporarily update our environment PATH variable so we can access java everywhere
PS C:\Users\adas\clojure> $env:path+=";" + (Join-Path $PWD  "java11\bin") 

PS C:\Users\adas\clojure> java -version # did it work?
openjdk version "11.0.1" 318-1-16 # it did
...
```
Great, `java` is ready.

# Getting Clojure
Now we can finally get ourselves Clojure. Like most Java programs it ships as a JAR.  
JARs are basically ZIP files.  
Most code and code-like stuff in the Java world is distributed as JARs.  
So let's get a JAR for Clojure.

 ```powershell
 PS C:\Users\adas\clojure> wget http://repo1.maven.org/maven2/org/clojure/clojure/1.8.0/clojure-1.8.0.jar -OutFile clojure.jar

# let's take a quick peek inside the jar
# (notice how we use a function for extracting Zip Files, because JARs are just Zips)
 PS C:\Users\adas\clojure> [System.IO.Compression.ZipFile]::ExtractToDirectory((Join-Path $PWD "/clojure.jar"),
 (Join-Path $PWD "jar-disassembly")) 
 # we extracted the jar to jar-disassembly, normally you don't ever manually extract a jar 
 # but we really want to see what's inside

# let's see the general dir structure
PS C:\Users\adas\clojure> tree jar-disassembly 
...
├───clojure
│   ├───asm
│   │   └───commons
│   ├───core
│   │   ├───protocols
│   │   └───proxy$clojure
│   │       └───lang
...
└───META-INF
    └───maven
        └───org.clojure
            └───clojure

# now let's see full paths
 PS C:\Users\adas\clojure> tree /F jar-disassembly 
 ...
│   │       PersistentHashMap$INode.class
│   │       PersistentHashMap$NodeIter.class
│   │       PersistentHashMap$NodeSeq.class
│   │       PersistentHashMap$TransientHashMap.class
│   │       PersistentHashMap.class
│   │       PersistentHashSet$TransientHashSet.class
│   │       PersistentHashSet.class
│   │       PersistentList$EmptyList$1.class
│   │       PersistentList$EmptyList.class
│   │       PersistentList$Primordial.class
│   │       PersistentList.class
...
 # interesting, mostly a lot of .class files
 # this is JVM's (Java Virtual Machine) standard format for bytecode
 # also note how everything is nested in directories
 # as we'll later see directories are very important in the Java world
  
 # to make use of our jar we need to put it on java's "classpath", it's like our system's PATH but for java
 PS C:\Users\adas\clojure> java -cp clojure.jar
 ...
 Usage: java [options] <mainclass> [args...]
            (to execute a class)
...
# but this didn't do anything, we have to indicate a class <mainclass> to run

 PS C:\Users\adas\clojure> java -cp clojure.jar clojure.main
 Clojure 1.8.0
 user=> "Nice, clojure.main starts a Clojure REPL! (Read, Eval, Print Loop)"
"Nice, clojure.main starts a Clojure REPL! (Read, Eval, Print Loop)"
 user=> "Let's Ctrl-C out of here"

# Actually JAR's META-INF/MANIFEST.MF will usually specify a default Main-Class, here clojure.main
PS C:\Users\adas\clojure> cat jar-disassembly/META-INF/MANIFEST.MF
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Created-By: Apache Maven
Built-By: hudson
Build-Jdk: 1.6.0_20
Main-Class: clojure.main

# so if we do -jar instead, java will automatically run Main-Class - clojure.main
PS C:\Users\adas\clojure> java -jar clojure.jar 
 ...
user=> "Back to the Clojure REPL"
"Back to the Clojure REPL"
 ```
 ```clojure
 user=> 50 ;; <= this is a number literal
50

 user=> (+ 50 1) ;; this invokes function + with arguments 50 and 1
 51

 user=> (type 60) ;; a Clojure number is just a java.lang.Long
 java.lang.Long

user=> (def v ["a" "vector"]) ;; square brackets denote vectors
#'user/v ;; this is a #'var, we'll learn what vars are later

user=> v 
["a" "vector"]

user=> (type v)
clojure.lang.PersistentVector

user=> (first v) ;; first returns the first element of the vector
"a"

user=> (nth v 0) ;; if you want to access by an index
"a"

user=> (v 0) ;; but this is weird, a vector itself is also a function! A function just like "nth". Interesting.
"a"

user=> (second v)
"vector"

;; now this is a "map" literal, it's very similar to JavaScript's object literals
 user=> (def m {:key "value", :another-key "almost like js right?"}) 
#'user/m

user=> m
{:key "value", :another-key "almost like js right?"}

user=> (type m)
clojure.lang.PersistentArrayMap

;; but a key, unlike javascript, isn't just notational sugar, 
;; a key, keyword actually, is a first-class value type in clojure
user=> :key 
:key

user=> (type :key)
clojure.lang.Keyword

;; Clojure maps take any basic Clojure values as keys, they don't have to be keywords
user=> {1 "val"} 
{1 "val"}

user=> {[1 2] "val"} ;; even vectors work
{[1 2] "val"}

user=> (get m :key) ;; get takes a collection and a key and returns a value
"value"

user=> (m :key) ;; like vectors, maps are also invokable as a function, this is roughly equivalent to (get m :key)
"value"

user=> ({[1 2] "val"} [1 2])
"val"

user=> (:key m) ;; :keywords are also invokable as a function! this is also roughly equivalent to (get m :key)
"value"

;; but other simple types aren't, you cannot invoke a number as a function
;; this is one of the reasons why :keywords are the preferred type for map keys
user=> (1 {1 "val"}) 
ClassCastException class java.lang.Long cannot be cast to class clojure.lang.IFn 

user=> (first {:key "value"}) ;; so what's the "first" element of a map?
[:key "value"] ;; a vector containing the first key/val?

user=> (type (first {:key "value"})) 
clojure.lang.MapEntry ;; well, almost

user=> (first (first {:key "value"})) ;; works a lot like a vector would
:key 

user=> (key (first {:key "value"})) ;; but we can do this
:key

user=> (val (first {:key "value"})) ;; and this
"value"

;; let's talk about symbols now
user=> 'a-symbol ;; this is a "quoted" symbol
a-symbol ;; why did we "quote" it?

user=> a-symbol ;; if we don't Clojure will try to "resolve" the symbol and retrieve it's value
CompilerException java.lang.RuntimeException: Unable to resolve symbol: a-symbol in this context

user=> m ;; m doesn't throw because we've previously defined m 
{:key "value", :another-key "almost like js right?"}

user=> (resolve 'm) ;; we can manually resolve m
#'user/m

user=> (resolve 'a-symbol)
nil ; nil as in nothing - we cannot resolve a-symbol to anything

user=> (type #'user/m) ;; so what is #'user/m
clojure.lang.Var ;; a var

user=> 'user/m ;; this is a symbol too
user/m  ;; what's this "user" part?

;; user is a namespace, the "namespace-qualified" symbol user/m has namespace "user"
user=> (namespace 'user/m) 
"user"

user=> (name 'user/m) ;; and name "m"
"m"

user=> (namespace 'm) ;; a bare symbol "m" isn't "namespace-qualified", hence nil
nil

;; namespaces are a very important concept in clojure, everything exists in a namespace
user=> *ns* ;; special symbol *ns* contains the current namespace
#object[clojure.lang.Namespace 0xf8908f6 "user"]

;; this is why we're seeing this "user" thing in our REPL indicating the current namespace
user=> (ns other) ;; we jump to a different ns
nil

other=> m ;; m is undefined here
CompilerException java.lang.RuntimeException: Unable to resolve symbol: m in this context

other=> user/m ;; we could still access the old m by namespace-qualifying our symbol
{:key "value", :another-key "almost like js right?"}

other=> (ns user) ;;ok back to user

; libraries in Clojure will come in their own namespaces, Clojure ships with some built-in ones
user=> (require 'clojure.string) ;; unloaded namespaces have to be required first, like clojure.string
nil

user=> (ns-publics 'clojure.string) ; let's check what clojure.string defines
{ends-with? #'clojure.string/ends-with?, capitalize #'clojure.string/capitalize, reverse #'clojure.string/reverse, join #'clojure.string/join, replace-first #'clojure.string/replace-first, starts-with? #'clojure.string/starts-with?, escape #'clojure.string/escape, last-index-of #'clojure.string/last-index-of, re-quote-replacement #'clojure.string/re-quote-replacement, includes? #'clojure.string/includes?, replace
#'clojure.string/replace, split-lines #'clojure.string/split-lines, lower-case #'clojure.string/lower-case, trim-newline #'clojure.string/trim-newline, upper-case #'clojure.string/upper-case, split #'clojure.string/split, trimr #'clojure.string/trimr, index-of #'clojure.string/index-of, trim #'clojure.string/trim, triml #'clojure.string/triml, blank? #'clojure.string/blank?}

;clojure.string/split looks cool but how do we use it?
user=> (doc clojure.string/split) ; thankfully the REPL has a doc function that'll help
clojure.string/split
([s re] [s re limit])
  Splits string on a regular expression.  Optional argument limit is
  the maximum number of splits. Not lazy. Returns vector of the splits.

user=> (clojure.string/split "A string we want to split into words" #" ") 
;;  #"regex" <= this is a regex literal (think /regex/ in js)
["A" "string" "we" "want" "to" "split" "into" "words"]

user=> (require '[clojure.string :as s]) ; to save ourselves typing we can require a namespace under a shortened name
nil

user=> (s/split "A string we want to split into words" #" ")
["A" "string" "we" "want" "to" "split" "into" "words"]

user=> (resolve 's/split) ;but it will still resolve to the full name
#'clojure.string/split

user=> (require '[clojure.string :as s :refer [split]]) ; require split directly into our namespace
nil

user=> (resolve 'split) ; still nicely resolves to the real deal
#'clojure.string/split

user=> (require 'main) ; what if we require a non-existent namespace?
FileNotFoundException Could not locate main__init.class or main.clj on classpath.  clojure.lang.RT.load (RT.java:456)

;; interesting, so java was looking for main__init.class or main.clj on the classpath
;; so what if there actually was main.clj on the classpath?
;; let's add it, unfortunately this means we have to Ctrl-C out of here

;; create main.clj - we explicitly set the encoding because some (like UTF16) may cause issues
PS C:\Users\adas\clojure> Out-File -Encoding ASCII main.clj

PS C:\Users\adas\clojure> ./main.clj
;; should open main.clj in your text editor, or it might ask you to pick a program
;; if you don't have a text editor just pick Notepad, it'll do for now
```
In `main.clj`:
```clojure
;; C:\users\adas\clojure\main.clj     remember to save :) 
(ns main)

(def nine 9)
```

Back to PowerShell:
```clojure
;; now we'll also add current directory (".") to the classpath so java can find main.clj
PS C:\Users\adas\clojure> java -cp "clojure.jar;." clojure.main

user=> (require 'main)
nil

user=> main/nine
9 ; great!
```
Don't quit the REPL, but let's change our file a little bit:
```clojure
;; main.clj      again, remember to save
(ns main)

(def nine 9)

(println "hello world")
```
Back to the REPL:
```clojure
user=> (require 'main) ; hmmm where's our println?
nil

user=> (require 'main) ; a namespace is loaded only once, requiring twice does nothing
nil

user=> (require '[main :reload :all]) ; but we can :reload :all to trigger a re-evaluation
hello world ; nice!
nil
```

Remember, we want to count dev.to headlines' most frequently occurring words.  
A web scraping library would come in handy. After a bit of googling [enlive](https://github.com/cgrand/enlive) seems like a good choice.
But how do we get it? Onto the next part...

# lein
Does Clojure have an `npm` equivalent (or `pip` or `gem`)? Kind of.  
 [lein](https://leiningen.org/) is a lot like `npm`, but again - to understand we have to go back to the java world.  

Java already has an arguably more powerful build and dependency management tool - __Maven__<sup id="a2">[2](#f2)</sup>.  
Like `npm`, __Maven__ can ingest our dependency specs and download relevant __artifacts__ - usually JAR files containing code.  
But just like the `java` command, __Maven's__ cli interface - `mvn` isn't easy.  
That's where `lein` comes in<sup id="a3">[3](#f3)</sup>. 
It will fetch dependencies with __Maven__, generate a classpath and run `java`, so we don't have to manually craft a very complex `java` command by hand. And much, much more!

# Getting lein
As per [leiningen.org/#install](https://leiningen.org/#install) for windows we need to place [lein.bat](https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein.bat) on our `PATH`:
```powershell
PS C:\Users\adas\clojure> wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein.bat -OutFile lein.bat

PS C:\Users\adas\clojure> ./lein.bat self-install # lein will install itself
Downloading Leiningen now...

PS C:\Users\adas\clojure> ./lein.bat # should work now
...
PS C:\Users\adas\clojure> $env:path+=";" + $PWD # temporarily add it to our PATH so it's available everywhere

PS C:\Users\adas\clojure> lein # make sure it's available now
... 

# we create "project.clj" - it's a lot like npm's "package.json"
PS C:\Users\adas\clojure> Out-File -Encoding ASCII project.clj

PS C:\Users\adas\clojure> ./project.clj 
# should open project.clj in your default editor
```

In `project.clj`:
```clojure
;; C:\Users\adas\clojure\project.clj
(defproject devto-words "0.0.1"
  :dependencies [])
```
This defines a project `devto-words` version `0.0.1` with no dependencies.  
First we'd like to pull in Clojure itself, right?  
We need to add `[org.clojure/clojure "1.8.0"]`<sup id="a4">[4](#f4)</sup> to our dependencies. `clojure` and  `"1.8.0"` makes sense, but why the `org.clojure` namespace?   
When lein interprets our dependencies it will use the namespace `org.clojure` as a Maven `groupId`, name `clojure` as `artifactId` and `"1.8.0"` as `version`.

If we go to [Maven Central's search page](https://search.maven.org/)<sup id="a5">[5](#f5)</sup> and [look for "clojure"](https://search.maven.org/artifact/org.clojure/clojure/1.8.0/jar) you can confirm that there is indeed such an artifact, a full xml spec is given:
```xml
<dependency>
  <groupId>org.clojure</groupId>
  <artifactId>clojure</artifactId>
  <version>1.8.0</version>
</dependency>
```
This is what lein will understand the dependency vector `[org.clojure/clojure "1.8.0"]` to mean.

[Enlive's](https://github.com/cgrand/enlive#artifact) github README [already gives us a lein-style dependency vector](https://github.com/cgrand/enlive#artifact): `[enlive "1.1.6"]`. Great!  
So if we add Clojure and enlive, our project.clj should end up looking like this:
```clojure
;; C:\Users\adas\clojure\project.clj
(defproject devto "0.0.1"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]])
```
By default `lein` adds `src` to java's classpath, having your code under `src` is a standard practice, so:
```powershell
PS C:\Users\adas\clojure> mkdir src # make directory src

PS C:\Users\adas\clojure> mv main.clj src/ # move our main.clj to src/, remember to close main.clj in your editor

PS C:\Users\adas\clojure> src/main.clj # should open main.clj at it's new location
```

Now when we start the REPL with `lein` it will first download our dependencies from Maven Central et al.<sup id="a5">[5](#f5)</sup>, put them on java's classpath, and finally start the REPL:
```clojure
PS C:\Users\adas\clojure> lein repl # like I promised, lein is downloading dependencies first
Retrieving org/clojure/clojure/1.8.0/clojure-1.8.0.pom from central
...
nREPL server started on port 56786 on host 127.0.0.1 - nrepl://127.0.0.1:56786
...
REPL-y 0.4.3, nREPL 0.5.3
Clojure 1.8.0
OpenJDK 64-Bit Server VM 11.0.1+13
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
    Exit: Control+D or (exit) or (quit)
 Results: Stored in vars *1, *2, *3, an exception in *e

;; you'll see there's a lot more output when starting this REPL
;; by default lein loads nREPL - a much more feature-packed REPL
;; but for now we don't need to know much about this

user=> (require 'main) ;; still works
hello world
nil

;; confirm that we have enlive on our classpath as per https://github.com/cgrand/enlive#quickstart-tutorial
user=> (require '[net.cgrand.enlive-html :as html]) 
nil ; we do!
 
```

Without exiting the REPL let's make some changes to `main.clj`:
```clojure
(ns main)

(require '[net.cgrand.enlive-html :as enlive])
(require '[clojure.string :as s])
;; let's require ourselves a pretty printing function - pprint
;; we'll be looking at a lot of data, might get messy
(require '[clojure.pprint :refer [pprint]])

(def url "http://dev.to")
```

Back to the REPL:
```clojure
user=> (require '[main :reload :all]) 
nil

user=> (ns main) ; let's set our namespace to main
nil

main=> url
"http://dev.to" ;; oops we actually wanted https not http, change it in main.clj

main=> (require '[main :reload :all]) ;; reload our changes
nil

main=> url
"https://dev.to" ;; ok, all is good now

;; this will fetch the page and parse it (let's not concern ourselves with how it works)
main=> (def document (enlive/html-resource (java.net.URL. url))) 
#'main/document
;; if you really want to see what's in there (pprint document), it'll print A LOT of stuff


;; again how this works is beyond the scope of this guide
;; but it's basically like running document.querySelectorAll(".single-article h3") or $$(".single-article h3") on a webpage
;; it should get us all the right headline elements from dev.to
main=> (def headers (enlive/select document [:.single-article :h3])) 
;; again (pprint headers) if curious

main=> (first headers)
{:tag :h3, :attrs nil, :content ("...")}

main=> (first (:content (first headers))) 
"\n              Freelancing 11: How to get started\n            "
;; what a nasty string

;; let's put the string in a variable so it's easier to work with
main=> (def sample-headline (first (:content (first headers))))
#'main/sample-headline

main=> (s/split sample-headline #"[\n ]+")
["" "Freelancing" "11:" "How" "to" "get" "started"]

main=> (s/split sample-headline #"[\n :]+")
["" "Freelancing" "11" "How" "to" "get" "started"]
;; uh we don' want that ""
;; but we're also not smart enough to make a better regexp
;; let's figure out a hack...

;; an identity fn returns it's input, basically does nothing
main=> (identity 3)
3

;; filter applies a function to every element in a collection
;; and removes any elements that return falsy values (think Array.filter in JavaScript)
main=> (filter identity [1 2 3 nil false ""])
(1 2 3 "") ;; note that both nil and false are "falsy" values in Clojure

main=> (not-empty "string") ;; not-empty returns it's input if it's not empty
"string"

main=> (not-empty "") ;; but returns nil if it is
nil

main=> (filter not-empty (s/split sample-headline #"[\n :]+"))
("Freelancing" "11" "How" "to" "get" "started")
;; if you're confused a javascript equivalent for above would be:
;; " Freelancing 11: How to get started".split(/[\n ]+/).filter(x=>x)   empty strings are falsy in js so no need for not-empty

;; instead of doing (first (:content (first headers)))
;; we can compose the two functions into one with comp
main=> ((comp first :content) (first headers))
"\n              Freelancing 11: How to get started\n            "

;; now we're doing the same thing for all headers with map
;; map applies a function to every element of a collection
;; and returns a new collection with each result
main=> (pprint (map (comp first :content) headers))
("\n              Freelancing 11: How to get started\n            "
 "\n            \n  The UX design pyramid with the user needs\n\n        \"
...

;; let's turn our piece of code that splits strings into words into a function
;; we create function tokenize that takes one argument s
main=> (defn tokenize [s] (filter not-empty (s/split s #"[\n :]+")))
#'main/tokenize

;; verify that it still works (remember you should be retyping!)
main=> (tokenize " a test sentence ")
("a" "test" "sentence") ;; it does!

;; so now for every header we call :content, then call first, and then tokenize
main=> (pprint (map (comp tokenize first :content) headers))
(("Freelancing" "11" "How" "to" "get" "started")
 ("The" "UX" "design" "pyramid" "with" "the" "user" "needs")
 ("Why" "Bandwidth" "Still" "Matters")
 ("Using" "API" "first" "and" "TDD" "for" "your" "next" "library")
 ("7"

;; now let's flatten these word lists into one
main=> (pprint (flatten (map (comp tokenize first :content) headers)))
("Freelancing"
 "11"
 "How"
 "to"

;; we're very lucky, clojure already comes with a function frequencies
;; it returns a map, mapping values to the times they occur in a collection
main=> (pprint (frequencies (flatten (map (comp tokenize first :content) headers))))
{"Interviews" 1,
 "Why" 1,
...}
;; our code is getting really ugly, so many function applications don't look pretty
;; Clojure has a ->> "thread last macro"
;; we can use it to rewrite in a way reads a little more naturally
main=> (->> headers (map (comp tokenize first :content)) flatten frequencies pprint)
{"Interviews" 1,
 "Why" 1,
...} 
;; it works, but what's going on???
;; ->> is a macro, for complete docs see (https://clojuredocs.org/clojure.core/-%3E%3E)
;; we can can examine what a macro expands to by doing macroexpand:
main=> (macroexpand '(->> headers (map (comp tokenize first :content)) flatten frequencies pprint)) 
(pprint (frequencies (flatten (map (comp tokenize first :content) headers)))) ;; exactly like before!
;; note how we had to quote the argument to macroexpand with '
;; quoting stops any evaluation from happening, so we can treat this piece of code as data
;; don't worry if it's still confusing, eventually it'll become second nature

;; so now all we need to do is sort our results
;; again, clojure already has a neat function - sort-by (detailed docs https://clojuredocs.org/clojure.core/sort-by)
main=> (->> headers (map (comp tokenize first :content)) flatten frequencies (sort-by val) pprint)
(...
 ["Gift" 2]
 ["in" 2]
 ["In" 3]
 ["for" 3]
 ["5" 3]
 ["a" 3]
 ["A" 3]
 ["your" 3]
 ["to" 3]
 ["of" 4]
 ["How" 4]
 ["the" 5]) 
 ;; yay this is what we wanted all along, 
 ;; we could improve our tokenize to exclude words like the, a, of...
 ;; but we'll leave this as an exercise for the reader
``` 

Let's clean up our `main.clj` now that we're almost done:

```clojure
;; C:\Users\adas\clojure\src\main.clj
(ns main)

(require '[net.cgrand.enlive-html :as enlive])
(require '[clojure.string :as s])
(require '[clojure.pprint :refer [pprint]])

(def url "https://dev.to")

(defn tokenize [s]
  (filter not-empty (s/split s #"[\n :]+")))

(def document (enlive/html-resource (java.net.URL. url)))

(def headers (enlive/select document [:.single-article :h3]))

(def top-words
  (->> headers
       (map (comp tokenize first :content))
       flatten
       frequencies
       (sort-by val)))

(defn print-top-words []
  (doseq [w top-words] ;; doseq is like forEach in javascript 
    (println (key w) (val w))))

(defn -main [] ;; we will explain this shortly
  (println "Will print top words in a sec...")
  (print-top-words))
```

In REPL
```clojure
main=> (require '[main :reload :all])
nil
main=> (print-top-words)
Interviews 1
... ;NICE! Ctrl+D out of the REPL
```

Let's tell `lein` that main is our entry-point namespace:
```clojure
;; C:\users\adas\clojure\project.clj
(defproject devto-words "0.0.1"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]]
  :main main)
```
By convention the main function of a namespace is `-main` (which we've fortunately just added).

Now:
```powershell
PS C:\Users\adas\clojure> lein run #this will load main and run main/-main now
Interviews 1
...
```
Final `main.clj` cleanup:
```clojure
(ns main ;; checkout https://clojuredocs.org/clojure.core/ns to understand what happened here
  (:gen-class)
  (:require
   [net.cgrand.enlive-html :as enlive]
   [clojure.string :as s]))

(def url "https://dev.to")

;; turn everything into functions so we only fetch stuff when actually calling print-top-words rather than on load 
(defn fetch-document [] 
  (enlive/html-resource (java.net.URL. url)))

(defn tokenize [s]
  (filter not-empty (s/split s #"( +|\n|:)+")))

(defn get-top-words []
  (->> (enlive/select (fetch-document) [:.single-article :h3])
       (map (comp tokenize first :content))
       flatten
       frequencies
       (sort-by val)))

(defn print-top-words []
  (doseq [w (get-top-words)]
    (println (key w) (val w))))

(defn -main []
  (println "Will print top words in a sec...")
  (print-top-words))
```
```powershell
PS C:\Users\adas\clojure> lein run # make sure it still works
Interviews 1
...

PS C:\Users\adas\clojure> lein jar # we can build a jar of our code
...
Compiling main
Created C:\Users\adas\clojure\target\devto-words-0.0.1.jar

PS C:\Users\adas\clojure> java -jar C:\Users\adas\clojure\target\devto-words-0.0.1.jar 
# we can't run this jar because it contains just our code, Clojure itself isn't included
Exception in thread "main" java.lang.NoClassDefFoundError: clojure/lang/Var
        at main.<clinit>(Unknown Source)
Caused by: java.lang.ClassNotFoundException: clojure.lang.Var
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:583)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:178)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521)
        ... 1 more

# instead we can build a fat jar containing everything needed to run our program
PS C:\Users\adas\clojure> lein uberjar 
...
Created C:\Users\adas\clojure\target\devto-words-0.0.1-standalone.jar

# should work as expected now
PS C:\Users\adas\clojure> java -jar C:\Users\adas\clojure\target\devto-words-0.0.1-standalone.jar 

# lein install installs our project into the local Maven repo (usually located at ~/.m2/repository)
# allowing us to depend on [devto-words "0.0.1"] in other projects on our machine
PS C:\Users\adas\clojure> lein install
...
Wrote C:\Users\adas\clojure\pom.xml
Installed jar and pom into local repo.

# to see a complete dependency tree for our project:
PS C:\Users\adas\clojure> lein deps :tree
 [clojure-complete "0.2.5" :exclusions [[org.clojure/clojure]]]
 [enlive "1.1.6"]
   [org.ccil.cowan.tagsoup/tagsoup "1.2.1"]
   [org.jsoup/jsoup "1.7.2"]
 [nrepl "0.5.3" :exclusions [[org.clojure/clojure]]]
   [nrepl/bencode "1.0.0"]
 [org.clojure/clojure "1.8.0"]
# notice how enlive itself pulls in 2 dependencies
# also we never asked for clojure-complete and nrepl
# lein's default "profile" pulls in some extras

# we can rerun deps :tree with a profile "provided" which should only have essentials
PS C:\Users\adas\clojure> lein with-profile provided deps :tree
 [enlive "1.1.6"]
   [org.ccil.cowan.tagsoup/tagsoup "1.2.1"]
   [org.jsoup/jsoup "1.7.2"]
 [org.clojure/clojure "1.8.0"]
# just what we asked for

# let's see what a bare classpath looks like
PS C:\Users\adas\clojure> lein with-profile provided classpath
C:\Users\adas\clojure\test;C:\Users\adas\clojure\src;C:\Users\adas\clojure\resources;C:\Users\adas\clojure\target\classes;C:\Users\adas\.m2\repository\org\clojure\clojure\1.8.0\clojure-1.8.0.jar;C:\Users\adas\.m2\repository\enlive\enlive\1.1.6\enlive-1.1.6.jar;C:\Users\adas\.m2\repository\org\ccil\cowan\tagsoup\tagsoup\1.2.1\tagsoup-1.2.1.jar;C:\Users\adas\.m2\repository\org\jsoup\jsoup\1.7.2\jsoup-1.7.2.jar
# mostly makes sense

# but we like to double-check everything
PS C:\Users\adas\clojure> java -cp 'C:\Users\adas\clojure\test;C:\Users\adas\clojure\src;C:\Users\adas\clojure\resources
;C:\Users\adas\clojure\target\classes;C:\Users\adas\.m2\repository\org\clojure\clojure\1.8.0\clojure-1.8.0.jar;C:\Users\
adas\.m2\repository\enlive\enlive\1.1.6\enlive-1.1.6.jar;C:\Users\adas\.m2\repository\org\ccil\cowan\tagsoup\tagsoup\1.2
.1\tagsoup-1.2.1.jar;C:\Users\adas\.m2\repository\org\jsoup\jsoup\1.7.2\jsoup-1.7.2.jar' main
Will print top words in a sec... 
# ok it works!
# note how this time instead of running class clojure.main we ran main
# this corresponds to the main namespace we worked on (clojure.main would start a REPL like before)
# if you noticed, in our final cleanup we added (:gen-class) to the ns form
# this makes sure a corresponding Java class is generated for our namespace

# the last lein feature we'll explore is "new", it generates scaffoldings for new projects.
PS C:\Users\adas\clojure> lein new my-project
Generating a project called my-project based on the 'default' template.
The default template is intended for library projects, not applications.
To see other templates (app, plugin, etc), try `lein help new`.

PS C:\Users\adas\clojure> tree /F my-project
...
│   .gitignore
│   .hgignore
│   CHANGELOG.md
│   LICENSE
│   project.clj
│   README.md
│
├───doc
│       intro.md
│
├───resources
├───src
│   └───my_project
│           core.clj
│
└───test
    └───my_project
            core_test.clj          
# this is what a proper Clojure project looks like 
```
# Thank you
We didn't learn much Clojure.  
But we learned many little details that most won't guides won't teach you.  
Now you can continue learning with confidence. You __already understand the confusing parts__, congrats!

Look at <sup id="a6">[footnote 6](#f6)</sup> if you want to make this installation permanent.  
Checkout the links for __where to go next__.   

# Thank me
__If you enjoyed this let me know__ by 💬commenting💬, ❤liking❤, ⭐starring⭐ on [github](https://github.com/adasomg/the-missing-clojure-intro),  
or by 👣[following the author on twitter](https://twitter.com/adasomg)👣.  
Otherwise I won't know and you'll never see a similar guide from me again 😭

# Links & footnotes

## links
### learning materials
- [Clojure cheatsheet - always have this open, the function you're looking for is probably on this list](https://clojure.org/api/cheatsheet)
- [ClojureDocs.org](https://clojuredocs.org)
- [Official Clojure getting started docs](https://clojure.org/guides/getting_started)
- [Enlive quickstart](https://github.com/cgrand/enlive#quickstart-tutorial)
### community
- [Clojurians slack - if you have a problem they'll probably help you out if you ask](https://clojurians.slack.com/)
- [ClojureVerse - a Clojure forum, beginner friendly](https://clojureverse.org/)
### lein
- [Sample project.clj showing lein's many features by example](https://github.com/technomancy/leiningen/blob/master/sample.project.clj)
- [lein's FAQ](https://github.com/technomancy/leiningen/blob/stable/doc/FAQ.md)
### editors & plugins
- [Clava - a Clojure extension for VS code ](https://marketplace.visualstudio.com/items?itemName=cospaia.clojure4vscode)
- [Cursive - a dedicated Clojure IDE, probably the best out-of-the-box experience](https://cursive-ide.com/)
- [Cider - Emacs Clojure IDE](https://docs.cider.mx/en/latest/)
### Misc & Java-related
- [What is Maven?](https://maven.apache.org/what-is-maven.html)
- [Clojars repository](https://clojars.org/)
- [Official docs on java's classpath](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/classpath.html)
- [Official docs on the many commands that ship with the JDK](https://docs.oracle.com/javase/9/tools/tools-and-command-reference.htm#JSWOR596)
- [Official docs on what JARs really are](https://docs.oracle.com/javase/tutorial/deployment/jar/basicsindex.html)
- [A tutorial on the PowerShell ZipFile stuff](https://blogs.technet.microsoft.com/heyscriptingguy/2015/08/14/working-with-compressed-files-in-powershell-5/)

## footnotes

<b id="f1">1</b> Yes, these PowerShell commands are weird. But they enable this guide to work even on the most basic Windows machine. No extra software needed. [↩](#a1)  

<b id="f2">2</b> [What is Maven?](https://maven.apache.org/what-is-maven.html) [↩](#a2)   

<b id="f3">3</b> There are efforts to move away from lein, towards more lightweight solutions.
[See this](https://clojure.org/guides/deps_and_cli). But from a learner's perspective they suffer from the same shortcomings. Knowledge of the java ecosystem is assumed. Whether you end up using lein or something else, all the lessons you learn here apply. And for the time being you'll mostly see people use lein.[↩](#a3)  

<b id="f4">4</b> You probably want to use Clojure version `1.10.0` (current [Stable Release](https://clojure.org/community/downloads)) in a real project.  
This guide uses `1.8.0` as newer versions depend on [clojure.spec](https://clojure.org/about/spec) which isn't bundled in Clojure's JAR. Downloading [clojure.spec](https://clojure.org/about/spec) and adding it to the classpath would add unnecessary complexity to this guide.  
As you learned `lein` does all that for you. But for consistency we still used `1.8.0`.  
So if you change `1.8.0` to `1.10.0` in `project.clj` it'll work just fine.  
__To reiterate__ - __use__ `[org.clojure/clojure "1.10.0"]` next time.[↩](#a4)

<b id="f5">5</b> __Maven Central__ is Maven's main repository. 
But unlike _npm_, the _Maven_ world relies less on a single repository. 
In fact most Clojure libraries are hosted on [clojars](https://clojars.org/) rather than Maven Central.
Even cooler, when you use Maven, your machine also has its local Maven repository.  
You can install artifacts into your local repo and it works just like remote ones. 
`lein install` will do exactly that.[↩](#a5)  

<b id="f6">6</b>
If you want to make our java and lein installations permanent, move `java11` to a better location like `C:\java11` and `lein.bat` to, say, `C:\lein\lein.bat` then add `C:\java11\bin` and `C:\lein` to your PATH.  

Don't know how to do that? See this example:
```powershell
# you need to open this powershell prompt as an Administrator
PS C:\Windows\system32> cd ~

PS C:\Users\adas\clojure>

PS C:\Users\adas\clojure> mv java11 C:\java11

PS C:\Users\adas\clojure> mkdir C:\lein

PS C:\Users\adas\clojure> mv lein.bat C:\lein\

# should open the right windows menu for you
# Select Path under System Variables, click Edit then add C:\java11\bin and C:\lein
PS C:\Users\adas\clojure> rundll32 sysdm.cpl,EditEnvironmentVariables 

# restart powershell to reload PATH....
# make sure java and lein works
PS C:\Users\adas> java
PS C:\Users\adas> lein

# don't need this anymore
PS C:\Users\adas> rm clojure 
```
[↩](#a6)

# Credits
[Andy Fingerhut](https://github.com/jafingerhut) for [spotting crucial errors](https://github.com/adasomg/the-missing-clojure-intro/issues/1) and [making great suggestions](https://github.com/adasomg/the-missing-clojure-intro/issues/2).
