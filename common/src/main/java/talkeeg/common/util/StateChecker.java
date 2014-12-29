/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *      This file is part of talkeeg-parent.
 *
 *      talkeeg-parent is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      talkeeg-parent is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with talkeeg-parent.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.common.util;

import talkeeg.common.model.StreamMessageType;

import java.util.*;

/**
 * utility for checking possible states <p/>
 * threadsafe
 * Created by wayerr on 29.12.14.
 */
public final class StateChecker<T> {

    public static class Builder<T> {
        private final Map<T, Set<T>> transitions = new HashMap<>();

        @SafeVarargs
        public final Builder<T> transit(T source, T ... destinations) {
            if(source == null) {
                throw new IllegalArgumentException("source state is null");
            }
            if(destinations == null || destinations.length == 0) {
                throw new IllegalArgumentException("destinations states is null or empty");
            }
            this.transitions.put(source, new HashSet<>(Arrays.asList(destinations)));
            return this;
        }

        public Graph<T> build() {
            return new Graph<>(this);
        }
    }

    public static final <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Graph<T> {
        private final Map<T, Set<T>> transitions = new HashMap<>();

        Graph(Builder<T> b) {
            for(Map.Entry<T, Set<T>> transition: b.transitions.entrySet()) {
                this.transitions.put(transition.getKey(), Collections.unmodifiableSet(new HashSet<>(transition.getValue())));
            }
        }

        /**
         * test that transition from `source` to `destination` is possible
         * @param source
         * @param destination
         * @return
         */
        public boolean isPossible(T source, T destination) {
            if(destination == null) {
                throw new IllegalArgumentException("destination state is null");
            }
            final Set<T> possibles = getTransitions(source);
            return possibles.contains(destination);
        }

        /**
         * immutable set of possible transitions source `source`
         * @return
         */
        public Set<T> getTransitions(T source) {
            if(source == null) {
                throw new IllegalArgumentException("source state is null");
            }
            final Set<T> set = this.transitions.get(source);
            if(set == null) {
                return Collections.emptySet();
            }
            return set;
        }

        /**
         * test that transition from `source` to `destination` is possible, if transition is impossible then exception will be thrown
         * @param source
         * @param destination
         */
        public void checkPossibility(T source, T destination) {
            if(destination == null) {
                throw new IllegalArgumentException("destination state is null");
            }
            final Set<T> possible = getTransitions(source);
            if(!possible.contains(destination)) {
                throw new RuntimeException("Impossible transition:\n" + source + " -> " + destination +
                  "\n possible destinations: " + Arrays.toString(possible.toArray()));
            }
        }

        /**
         * create state checker with initial state
         * @param initialState
         * @return
         */
        public StateChecker<T> createChecker(T initialState) {
            return new StateChecker<>(this, initialState);
        }
    }

    private final Graph<T> graph;
    private final Object lock = new Object();
    private T current;

    StateChecker(Graph<T> graph, T current) {
        this.graph = graph;
        this.current = current;
    }

    public Graph<T> getGraph() {
        return graph;
    }

    /**
     * immutable set of possible transitions source `source`
     * @see talkeeg.common.util.StateChecker.Graph#getTransitions()
     * @return
     */
    public Set<T> getTransitions() {
        return this.graph.getTransitions(this.getCurrent());
    }

    public T getCurrent() {
        synchronized(this.lock) {
            return current;
        }
    }

    /**
     * test that transition from `source` to `destination` is possible
     * @see talkeeg.common.util.StateChecker.Graph#isPossible(Object, Object)
     * @param destination
     * @return
     */
    public boolean isPossible(T destination) {
        return this.graph.isPossible(this.getCurrent(), destination);
    }

    /**
     * test that transition from `source` to `destination` is possible, if transition is impossible then exception will be thrown
     * @see talkeeg.common.util.StateChecker.Graph#checkPossibility(Object, Object)
     * @param destination
     */
    public void checkPossibility(T destination) {
        this.graph.checkPossibility(this.getCurrent(), destination);
    }

    /**
     * change current state to `destinationState`
     * @param destinationState
     */
    public void transit(T destinationState) {
        synchronized(this.lock) {
            checkPossibility(destinationState);
            this.current = destinationState;
        }
    }
}
