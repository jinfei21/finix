package com.finix.gateway.common;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StateMachine<S> {

	private final Map<Key<S>,Function<Object,S>> transferMap;
	
	private final BiFunction<S, Object, S> defaultEventHandler;
	
	private final StateChangeListener<S> stateChangeListener;
	
	@Getter
	private  volatile S currentState;
	
	private StateMachine(S initialState,List<StateEventHandler<S>> handlers,BiFunction<S, Object, S> defaultEventHandler,StateChangeListener<S> stateChangeListener){
		this.currentState = requireNonNull(initialState);
		this.transferMap = handlers.stream().collect(toMap(
				handler -> handler.key,
				handler -> handler.mapper
				));
		this.defaultEventHandler = requireNonNull(defaultEventHandler);
		this.stateChangeListener = requireNonNull(stateChangeListener);
	}
	
	public void handle(Object event){
		S newState = stateMapper(event.getClass()).orElse(this::handleDefaultEvent).apply(event);
		
		S oldState = currentState;
		
		currentState = newState;
		
		stateChangeListener.onStateChange(oldState, currentState, event);
	}
	
	private S handleDefaultEvent(Object event){
		return this.handleDefaultEvent(event);
	}
	
	private Optional<Function<Object,S>> stateMapper(Class<?> eventClazz){
		Key<S> key = new Key<>(currentState,eventClazz);
		return Optional.ofNullable(this.transferMap.get(key));
	}
	
	private static class Key<S>{
		private final S state;
		private final Class<?> eventClazz;
		
		private Key(S state,Class<?> eventClazz){
			this.state = state;
			this.eventClazz = eventClazz;
		}
		
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Key<?> key = (Key<?>) o;
            return Objects.equals(state, key.state) && Objects.equals(eventClazz, key.eventClazz);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, eventClazz);
        }
	}
	
	private static class StateEventHandler<S>{
		private final Key<S> key;
		private final Function<Object,S> mapper;
		
        <E> StateEventHandler(S state, Class<E> eventClazz, Function<E, S> mapper) {
            this.key = new Key<>(state, eventClazz);
            this.mapper = event -> mapper.apply((E) event);
        }
	}
	
	public static final class Builder<S> {
	
		private final List<StateEventHandler<S>> stateEventHandlers = Lists.newArrayList();
		private BiFunction<S, Object, S> defaultEventHandler;
		private S initialState;
		private StateChangeListener<S> stateChangeListener = (oldState,newState,event) -> {};
		
		public Builder<S> initialState(S initialState){
			this.initialState = initialState;
			return this;
		}
		
		public <E> Builder<S> transition(S state,Class<E> eventClazz,Function<E,S> mapper){
			this.stateEventHandlers.add(new StateEventHandler<>(state, eventClazz, mapper));
			return this;
		}
		
		public <E> Builder<S> onDefaultEvent(BiFunction<S,E,S> mapper){
			this.defaultEventHandler = (state,event) ->mapper.apply(state,(E) event);
			return this;
		}
		
		public Builder<S> onStateChange(StateChangeListener<S> stateChangeListener){
			this.stateChangeListener = requireNonNull(stateChangeListener);
			return this;
		}
		
		public StateMachine<S> build(){
			return new StateMachine<>(initialState, stateEventHandlers, defaultEventHandler, stateChangeListener);
		}
	}
}
