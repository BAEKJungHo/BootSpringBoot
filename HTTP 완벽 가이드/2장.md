## URL

URL은 통합 자원 지시자라고 불리며 리소스의 위치를 가리킨다.

URL의 구성은 크게 `스킴 + 호스트 + 경로`로 구성된다.

http://www.joes-hardware.com/seasonal/index-fall.html

http가 스킴(어떻게) www.joes-hardware.com가 호스트(어디에) /seasonal/index-fall.html가 경로(무엇을)

즉 http 프로토콜을 사용하여 해당 호스트(서버 위치)에 /seasonal/index-fall.html에 있는 리소스를 가져오라는 의미이다.


URL 뒤에 ?로 시작하는것은 질의 문자열 혹은 쿼리 스트링 등이라고 불리는데, 이것은 데이터베이스 게이트웨이에 질의하는데 사용된다.

프래그먼트는 URL뒤에 #으로 시작하는데

예를들어 index.html 리소스를 작게 나누면 html안에있는 이미지 리소스, 동영상 리소스 등등이 있다. 프래그먼트는 잘게 나누어진 리소스에 바로 접근하기 위해 사용되며
프래그먼트는 서버에 전달되지 않고 서버에서 URL로 html 리소스를 전부 가져와서 a 태그의 # 기능 처럼, 해당 리소스의 위치로 HTML 페이지를 보여준다.

http://www.joes-hardware.com/seasonal/index-fall.html 를 절대 URL이라하며 상대 URL은 a 태그에 기술하는거와 같이 a href="./hammers.html"과 같은것을 상대
URL이라 한다.

상대 URL을 사용하면 리소스 집합을 쉽게 변경할 수 있다.
