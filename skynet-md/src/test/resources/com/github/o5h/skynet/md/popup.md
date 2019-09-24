# SkyNet

## Program Node: Title

|                 |                     |
|-----------------|---------------------|
|Title            | Popup               |
|Description      | Display popup on GUI|
|userInsertable   | true                |
|childrenAllowed  | false               |


### DataModel

|id       |type    | default |validator  |
|---------|--------|---------|-----------|
|msg      | string |         | [0...128] |
|title    | string | Message | [0...32]  |
|warning  | bool   | false   |           |
|error    | bool   | false   |           |
|blocking | bool   | false   |           |


### Script

```
popup('${message:string}',title='${title:string}', warning=${warning:bool}, error=${error:bool}, blocking=${blocking:bool)
```

### View

<table border="1">
<tr><td colspan="2">%Description%</td></tr>
<tr><td>${Message}</td><td><input type="text" id="msg"/></td></tr>
<tr><td>${Title}</td><td><input type="text" id="title"/></td></tr>
<tr><td>${Warning}</td><td><input type="checkbox" id="warning"/></td></tr>
<tr><td>%Error%</td><td><input type="checkbox" id="error"/></td></tr>
<tr><td>%Blocking%</td><td><input type="checkbox" id="blocking"/></td></tr>
</table>

## Function: Test

```
def test():
end
```

## Bundle

|      Key     |          Value                    |
|-------------:|:---------------------------------:|
|ID            |      skynet.popup                 |
|Version       |                            0.0.1  |
|Vendor        | Oleksandr Shaposhnikov            |
|ContactAddress| https://github.com/o5h/skynet     |

### License

```text
MIT License

Copyright (c) 2019 Oleksandr Shaposhnikov

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
```
