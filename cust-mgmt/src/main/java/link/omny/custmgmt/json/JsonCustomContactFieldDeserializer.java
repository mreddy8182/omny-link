package link.omny.custmgmt.json;

import java.util.List;
import java.util.Map.Entry;

import link.omny.custmgmt.model.CustomContactField;
import link.omny.custmgmt.model.CustomField;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonCustomContactFieldDeserializer extends
        JsonCustomFieldDeserializer<List<CustomContactField>> {

    protected CustomField newInstance(Entry<String, JsonNode> entry) {
        return new CustomContactField(entry.getKey(), entry.getValue().asText());
    }

}
