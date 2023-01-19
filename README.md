# 모임 관리 서비스

패키지 간 의존성

![](image/dependency.png)


<br>
<br>
<br>

### @Async 적용 시
@Async를 통해 비동기 작업을 진행하면, 다른 쓰레드에서 일어남으로 같은 Transaction을 사용할 수 없어짐.

https://okky.kr/articles/1033767

https://newwisdom.tistory.com/m/127

https://kobumddaring.tistory.com/m/42

```
@Async 어노테이션의 매커니즘을 자세히 설명한 글이 없어 그냥 소스 코드를 분석하는 게 빠릅니다.
BeanPostProcessor와 프록시의 동작 방법을 이해하신다는 가정 하에 간단히 설명드리면
 AsyncAnnotationBeanPostProcessor가 @Async 메서드를 가진 빈을 처리하는데요,
 @Async 어노테이션을 가진 모든 로직은
 결론적으로 AsyncExecutionInterceptor#invoke 에서 실제 로직을 Callable 객체로 감싼 후,
 AsyncExecutionAspectSupport#doSumit 에서 Callable로 감싼 로직을 CompletableFuture로 감싼 후, 
 TaskExecutor에서 대신 실행하도록 넘겨주는 것을 확인할 수 있습니다.

여기서 현재 프록시가 실행되는 쓰레드와 우리가 작성한 실제 로직이 실행되는 쓰레드가 분리되는 것을 직접 확인하실 수 있습니다.```

```

<br>
<br>
<br>


## instanceOf 분리하기
알림은 여러 종류가 있으며, 여러 종류의 이벤트를 통해 알림이 각각 생성되며,
알림이 가지고 있어야 할 정보들이 각기 다른 상황이었습니다.

즉 여러 종류의 이벤트 - 여러 종류의 알림 Entity 가 대응되는 상황이었으며
Entity별로 DTO와 Response가 다르게 존재하는 상황이었습니다.

처음에는 알림을 생성하는 이벤트를 추상 클래스로 정의하여 Common 패키지에 위치시켰습니다.
이후 각각의 도메인에서는 해당 이벤트를 상속받아 구현하였고, EventHandler를 정의하여 이벤트를 받으면 대응되는 알림을 생성하여 저장하도록 구현하였습니다.

이때 여러 종류의 알림 엔티티는 Notification 패키지에 위치하고 있었고, 다른 도메인에서는 Notification을 모르게 하기 위해 Notifiaction에서 이벤트에 대응되는 알림 엔티티를 instanceof를 통해 다음과 같이 구분하여 변환하였습니다.
![](image/instanceof.png)

또한 Entity는 Dto를 모르도록 구현하고 싶었기에(DTO는 Application Layer에 존재하므로, domain 계층의 Entity가 이를 알게된다면 domain -> application으로의 의존성이 생기기 때문입니다.), Entity -> DTO를 변환하는 과정에서도 이와 같이 instanceof를 사용하였습니다.

마찬가지로 Dto 역시 Presentation 계층의 Response를 모르게 하고 싶었기에 instanceof를 통해 구현하였습니다.
![](image/instanceof2.png)
![](image/instanceof3.png)

그러나 instanceof의 남용을 맏기 위해 조언도 구해보고 나름 생각을 해본 결과는 다음과 같습니다.


1. 우선 Template Method 패턴과 제네릭을 사용하여 Event handler에서 Notifiaction Entity를 생성하는 부분만을 추상 클래스로 남겨둔 후, 이를 저장하는 부분은 공통적으로 처리합니다.
2. instanceOf보다는 Entity가 DTO를 알게 하는 편이 나을 것 같았습니다.(복잡도가 훨씬 낮으며, 또한 알림의 종류가 많아질 경우 훨씬 실수할 위험도 줄어들 것이라 생각하였습니다.) 따라서 Noticiation이라는 추상 클래스에 toDto()라는 추상 메서드를 만들어 각 클래스별로 대응되는 DTO를 반환하게끔 구현합니다.
   1. 이 과정에서 DTO에 대한 인터페이스, 혹은 추상 클래스를 Domain 계층에 정의한 뒤, Application 계층에서는 해당 DTO를 구현하는 구조로 만들면 Domain에서 Application Layer로의 의존성을 없앨 수 있을 것이라 생각합니다.
3. 마찬가지로 Application Layer의 DTO 에서 Presentation Layer의 Response로 변환하는 과정 역시 DTO.toResponse()라는 추상 클래스를 통해 구현하려 합니다.
   1. 마찬가지로 이곳에서도 Response에 대한 추상 클래스를 Application 계층에 위치시킬 것인지가 고민입니다.
   2. Response 이라는 이름 자체가 Presentation 계층과 연관이 있으므로 어떻게 해야할지 고민중입니다.


