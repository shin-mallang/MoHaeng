# 모임 관리 서비스

패키지 간 의존성

![](image/dependency.png)


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

