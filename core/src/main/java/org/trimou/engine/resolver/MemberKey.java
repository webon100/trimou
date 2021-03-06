/*
 * Copyright 2013 Martin Kouba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.engine.resolver;

/**
 *
 * @author Martin Kouba
 */
final class MemberKey {

    private final Class<?> clazz;

    private final String name;

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    MemberKey(Class<?> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof MemberKey))
            return false;
        MemberKey other = (MemberKey) obj;
        if (clazz == null) {
            if (other.clazz != null)
                return false;
        } else if (!clazz.equals(other.clazz))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("MemberKey [clazz: %s, name: %s]", clazz, name);
    }

    /**
     * Simple factory method to obtain a new instance for the given context
     * object and name.
     *
     * @param contextObject
     * @param name
     * @return a new instance
     */
    static MemberKey newInstance(Object contextObject, String name) {
        return (contextObject instanceof Class<?>) ? new MemberKey(
                (Class<?>) contextObject, name) : new MemberKey(
                contextObject.getClass(), name);
    }

}
