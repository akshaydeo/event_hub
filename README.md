# EventHub

EventHub is a slim library to allow your different asynchronous components inside an app to communicate with each
other with very minimal overhead. Basic motivation of this idea was to allow activity to understand the state of its
different fragments inside your Android application.


## Architecture

EventHub is a singleton class which can be accessed through getInstance() method. Architecture is based on two
components, one is Event Generator and another is Event Listeners. Event generating components register their
action using registerEventHubAction method. And listeners register for listening the events using
registerListenerForAction method.
Action generator publish and action using actionHappened method, and it remains in the store throughout the lifecycle
 unless the action generator class un-registers itself. So that even if some new listener joins an event family, it
 gets the last message happened and can decide the state of itself.


```
                                     +--------------------+
                                     |                    |
                                 +---> Event Listener 1   |
                                 |   |                    |
+-------------+   +----------+   |   +--------------------+
|             |   |          |   |   +--------------------+
| Event       +---> EventHub +---+   |                    |
| Generator   |   |          |   +---> Event Listener 2   |
+-------------+   +----------+   |   |                    |
                                 |   +--------------------+
                                 |
                                 |   +--------------------+
                                 |   |                    |
                                 +---+ Event Listener n   |
                                     |                    |
                                     +--------------------+
```


## For using it with Android

You can build this library and use jar in libs to use it with your Android application. This library does not use any
 specific library related with Android. In EventHubAction you can use Bundle instead. Both EventHubAction and
 EventHubActionListener are templatized so you can use any kind of object to represent the data related with your
 action.

## Running example (Android APP)

I have used this library in AppSurfer (http://appsurfer.com) mobile app as well as our mobile SDK,
for fragment state communication. We had three fragments inside a ViewPager (each having its own type of filters)
showing apps related to the current category (somewhat similar to the Google Play store). Now to avoid using more
memory, I had enabled caching for only 1 invisible fragment and the third fragment would get destroyed as soon as it
leaves the cache enabled quota (using public void setOffscreenPageLimit (int limit)) of ViewPager. The category
selection dialog was a separate fragment and had its own lifecycle. Now I had to store the current selected category
by user so that as he traverses through the ViewPager he should see the updated category on each fragments.

In this scenario I used EventHub on the main activity, to which category fragment registers itself as category action
 event generator and all the fragments of the view pager register themselves as event listener for category action.
 So whenever a fragment gets resumed, it registers for the action, gets the latest message on that hub,
 and shows that category.

(This is very similar to what google play does, when it shows a tab bar with Top Free,
Top paid and on the leftmost side it has a category selection fragment).

## Licence

The MIT License (MIT)

Copyright (c) 2014 Akshay Deo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.