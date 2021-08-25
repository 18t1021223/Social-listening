package vn.com.sociallistening.manager.api.configurations;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class CustomAbstractMongoEvent<E> extends AbstractMongoEventListener<E> {

    @Autowired
    private MongoOperations mongoOperations;

    private final Class<?> domainClass;

    public CustomAbstractMongoEvent() {
        Class<?> typeArgument = GenericTypeResolver.resolveTypeArgument(this.getClass(), AbstractMongoEventListener.class);
        this.domainClass = typeArgument == null ? Object.class : typeArgument;
    }

    /*
    Using for beforeDelete
     */
    public Object getObject(Document document ) {
        Object valueDocument = document.get("_id");
        if (valueDocument != null) {
            Query query = new Query();
            if (valueDocument instanceof ObjectId)
                return mongoOperations.find(query.addCriteria(Criteria.where("_id").is(valueDocument)),  domainClass);
            else if (valueDocument instanceof Document && ((Document) valueDocument).containsKey("$in")) {
                List<ObjectId> listObject = (List<ObjectId>) ((Document) valueDocument).get("$in");
                return mongoOperations.find(query.addCriteria(
                        Criteria.where("_id").in(listObject)), domainClass);
            }
        }
        return null;
    }
}
