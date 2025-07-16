---
title: "[디자인 패턴] 팩토리 패턴(factory pattern)"
categories:
- CS
descriptions: 팩토리 패턴의 개념에 대해 간단히 포스팅 한 글입니다.
math: true
pin: false
mermaidd: true
tags:
- CS
---

# **팩토리 패턴(factory pattern)** 
<br>
> 팩토리 패턴은 객체 생성 로직을 별도의 클래스(또는 메서드)로 분리하여 캡슐화하는 추상화 패턴입니다. 
> 즉, 상속 관계에 있는 두 클래스에서 상위 클래스에서 중요한 뼈대 하위 클래스에서 객체 생성에 관한 구체적인 내용을 결정하는 구조입니다.
<br>
<br>



JavaScript의 `new Object()`의 예를 들자면 전달하는 값의 타입에 따라 다른 타입의 객체를 생성하는 팩토리 패턴이라는 것을 알 수 있습니다.

팩토리 패턴은 주로 객체 생성이 복잡하거나, 생성할 객체가 상황에 따라 달라지는 경우, 또는 유지보수와 확장성을 고려할 때 사용됩니다.

<br>
<br>


---

## 결제시스템에서 팩토리 패턴

개발할때의 팩토리 패턴을 예를 들자면 결제 시스템에 관한 예가 있습니다.
<br>
결제를 진행할때 카드결제, 카카오페이 결제, 네이버페이 결제등 여러 결제 시스템이 존재하므로 상황에 따라 적절하게 객체를 생성해야합니다.
<br>

```java
public class PaymentFactory {
    public static Payment getPayment(String method) {
        switch (method.toLowerCase()) {
            case "card": return new CardPayment();
            case "kakao": return new KakaoPayPayment();
            case "naver": return new NaverPayPayment();
            default: throw new IllegalArgumentException("지원하지 않는 결제 수단입니다.");
        }
    }
}
```
<br>

위의 코드와 같이 팩토리 패턴을 사용해 알맞은 객체를 입력해주면 따로 적용이 되어 추후 확장성도 보장할 수 있습니다.

<br>
<br>

---

## 팩토리 패턴의 주의점

하지만 팩토리 패턴이 무조건 좋은 건 아닙니다.
개발할때 소규모 프로젝트나 간단한 코드에 무조건 팩토리 패턴을 사용하게 되면 클래스의 수가 쓸데없이 많아지고 오히려 가독성이 떨어집니다.
<br>

  복잡한 객체 생성이 필요한 상황에서 사용하는 것이 좋습니다.
	
<br>
<br>

---

## BeanFactory
<br>

spring을 관리하는 상위 계층인 BeanFactory는 팩토리 패턴을 기반으로 만들어진 핵심 인터페이스입니다
Spring의 BeanFactory는 객체 생성뿐만 아니라 의존성 주입, 라이프사이클 관리, 스코프 관리도 담당하는 강화된 팩토리
<br>

BeanFactory의 구조를 보면 이처럼 interface로 추상화 구조로 이루어진 것을 알 수 있습니다.

```java
public interface BeanFactory {
	String FACTORY_BEAN_PREFIX = "&";
	Object getBean(String name) throws BeansException;
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;
	Object getBean(String name, Object... args) throws BeansException;

	<T> T getBean(Class<T> requiredType) throws BeansException;
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);
	boolean containsBean(String name);
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;
	
	@Nullable
	Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;
	
	String[] getAliases(String name);

}

```
<br>

```
BeanFactory (인터페이스)
   ↳ ApplicationContext (확장 인터페이스)
      ↳ AnnotationConfigApplicationContext 등 다양한 구현체
```
<br>
간단히 보면 spring은 위와 같은 구조로 객체 생성을 관리할 때도 팩토리 패턴이 들어가는 것을 알수 있습니다.
<br>
<br>

---
## 추상화 팩토리 패턴 vs 팩토리 메서드 패턴

<br>

비슷하지만 조금 다른 추상화 팩토리 패턴과 팩토리 메서드 패턴에 대해서도 간단하게 비교를 진행해보겠습ㄴ디ㅏ

<br>

---

### 추상화 팩토리 패턴

💡 핵심 아이디어
> 서로 관련된 여러 객체를 그룹으로 묶어 일관되게 생성하기 위한 인터페이스 제공

추상화 팩토리 패턴은 관련있는 여러 객체를 묶어서 생성할 수 있도록 추상화 시키는 개념입니다.
예를 들면 아까 설명했던 빈 팩토리가 해당됩니다.

<br>

---

### 팩토리 메서드 패턴
💡 핵심 아이디어
> 객체 생성을 서브클래스가 결정하도록 "메서드 하나"를 추상화하여 위임하는 패턴

객체를 만드는 메서드 하나를 자식이 오버라이딩해서 결정하게 합니다.
즉, 객체의 생성 방식을 자식 클래스가 정하는 방식입니다.


<br>

*이와 같이 비슷하지만 두 패턴은 역할에 따라 다르게 부르게도 합니다.*
