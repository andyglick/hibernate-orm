/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.envers.entities.mapper.id;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.envers.ModificationStore;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.tools.reflection.ReflectionTools;

import org.hibernate.property.Getter;
import org.hibernate.property.Setter;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class SingleIdMapper extends AbstractIdMapper implements SimpleIdMapperBuilder {
    private String beanPropertyName;
    private String propertyName;

    public SingleIdMapper() {
    }

    public SingleIdMapper(String beanPropertyName, String propertyName) {
        this.beanPropertyName = beanPropertyName;
        this.propertyName = propertyName;
    }

    public SingleIdMapper(String propertyName) {
        this.beanPropertyName = propertyName;
        this.propertyName = propertyName;
    }

    public void add(String propertyName, ModificationStore modStore) {
        if (this.propertyName != null) {
            throw new AuditException("Only one property can be added!");
        }

        this.propertyName = propertyName;
        this.beanPropertyName = propertyName;
    }

    public void mapToEntityFromMap(Object obj, Map data) {
        if (data == null || obj == null) {
            return;
        }

        Setter setter = ReflectionTools.getSetter(obj.getClass(), beanPropertyName);
        setter.set(obj, data.get(propertyName), null);
    }

    public Object mapToIdFromMap(Map data) {
        if (data == null) {
            return null;
        }

        return data.get(propertyName);
    }

    public Object mapToIdFromEntity(Object data) {
        if (data == null) {
            return null;
        }

        Getter getter = ReflectionTools.getGetter(data.getClass(), beanPropertyName);
        return getter.get(data);
    }

    public void mapToMapFromId(Map<String, Object> data, Object obj) {
        if (data != null) {
            data.put(propertyName, obj);
        }
    }

    public void mapToMapFromEntity(Map<String, Object> data, Object obj) {
        if (obj == null) {
            data.put(propertyName, null);
        } else {
            Getter getter = ReflectionTools.getGetter(obj.getClass(), beanPropertyName);
            data.put(propertyName, getter.get(obj));
        }
    }

    public void mapToEntityFromEntity(Object objTo, Object objFrom) {
        if (objTo == null || objFrom == null) {
            return;
        }

        Getter getter = ReflectionTools.getGetter(objFrom.getClass(), beanPropertyName);
        Setter setter = ReflectionTools.getSetter(objTo.getClass(), beanPropertyName);
        setter.set(objTo, getter.get(objFrom), null);
    }

    public IdMapper prefixMappedProperties(String prefix) {
        return new SingleIdMapper(propertyName, prefix + propertyName);
    }

    public List<QueryParameterData> mapToQueryParametersFromId(Object obj) {
        List<QueryParameterData> ret = new ArrayList<QueryParameterData>();

        ret.add(new QueryParameterData(propertyName, obj));

        return ret;
    }
}
