package com.puc.sistemasdevendas.model.services;

import com.puc.sistemasdevendas.model.entities.Item;
import com.puc.sistemasdevendas.model.exceptions.ForbidenException;
import com.puc.sistemasdevendas.model.exceptions.InternalErrorException;
import com.puc.sistemasdevendas.model.exceptions.NotFoundException;
import com.puc.sistemasdevendas.model.helpers.DecodeToken;
import com.puc.sistemasdevendas.model.repositories.ItemRepository;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DecodeToken decodeToken;
    private final Logger logger = Logger.getLogger(ItemService.class);

    public Item createItem(String token, Item item) {
        try {
            this.authorizeOperation(token, "create");
            return this.itemRepository.insert(item);
        } catch (Exception e) {
            this.logger.error("Failed to create item: " + e.getMessage());
            throw new InternalErrorException("Failed to create item: " + e.getMessage());
        }
    }

    public void deleteItem(String token, String itemId) {
        try {
            this.authorizeOperation(token, "delete");

            if (!this.itemRepository.existsById(itemId)) {
                throw new NotFoundException("Could not find item with id: " + itemId);
            }

            this.itemRepository.deleteById(itemId);
        } catch(Exception e) {
            this.logger.error("Failed to delete item: " + itemId + " | " + e.getMessage());
            throw e;
        }

    }

    private void authorizeOperation(String token, String operation) {
        String emailFromToken = this.decodeToken.getGetFromToken(token);

        if (!this.userService.isAdminRole(emailFromToken)) {
            this.logger.warn("An attempt to " + operation + " item was made but the user does not have permission: " + emailFromToken);
            throw new ForbidenException("Not authorized to perform this operation on item");
        }
    }
}
