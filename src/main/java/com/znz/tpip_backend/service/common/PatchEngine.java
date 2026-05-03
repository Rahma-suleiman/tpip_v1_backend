// package com.znz.tpip_backend.service.common;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Component;

// import java.lang.reflect.Field;

// @Component
// public class PatchEngine {

//     private final ModelMapper modelMapper;

//     public PatchEngine(ModelMapper modelMapper) {
//         this.modelMapper = modelMapper;
//     }

//     public <T, D> T patch(D dto, T entity) {

//         if (dto == null) return entity;

//         try {
//             Field[] fields = dto.getClass().getDeclaredFields();

//             for (Field field : fields) {
//                 field.setAccessible(true);

//                 Object value = field.get(dto);

//                 if (value != null) {

//                     Field entityField = entity.getClass()
//                             .getDeclaredField(field.getName());

//                     entityField.setAccessible(true);
//                     entityField.set(entity, value);
//                 }
//             }

//         } catch (Exception e) {
//             throw new RuntimeException("PATCH FAILED: " + e.getMessage());
//         }

//         return entity;
//     }
// }

// package com.znz.tpip_backend.service.common;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Component;

// @Component
// public class PatchEngine {

//     private final ModelMapper modelMapper;

//     public PatchEngine(ModelMapper modelMapper) {
//         this.modelMapper = modelMapper;
//     }

//     /**
//      * SAFE PATCH ENGINE (Recommended)
//      * - ignores null fields
//      * - copies only provided values
//      * - works for ALL DTOs and ENTITIES
//      */
//     public <D, T> T patch(D dto, T entity) {

//         if (dto == null || entity == null) return entity;

//         modelMapper.getConfiguration().setSkipNullEnabled(true);
//         modelMapper.map(dto, entity);

//         return entity;
//     }
// }
package com.znz.tpip_backend.service.common;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PatchEngine {

    private final ModelMapper modelMapper;

    public PatchEngine(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <D, T> T patch(D dto, T entity) {

        if (dto == null || entity == null) return entity;

        ModelMapper tempMapper = new ModelMapper();

        tempMapper.getConfiguration().setSkipNullEnabled(true);

        tempMapper.map(dto, entity);

        return entity;
    }
}