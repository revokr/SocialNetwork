package Repository;


import Domain.Entity;
import Domain.Friendship;
import Domain.Validator.ValidationException;

import java.util.List;
import java.util.Optional;

/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */

public interface Repository<ID, E extends Entity<ID>, F extends Friendship> {

    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    Optional<E> findOne(ID id);

    Optional<E> findByName(String firstname, String lastname);

    /**
     *
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     *
     * @param entity
     *         entity must be not null
     * @return null- if the given entity is saved
     *         otherwise returns the entity (id already exists)
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.     *
     */
    Optional<E> save(E entity);


    /**
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    Optional<E> delete(ID id);

    /**
     *
     * @param entity
     *          entity must not be null
     * @return null - if the entity is updated,
     *                otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidationException
     *             if the entity is not valid.
     */
    Optional<E> update(E entity);

    /**
     *
     * @param userID
     *          must be an existing ID
     * @param friend
     *          must be valid
     * @return null - if the friend is added
     *              otherwise returns the friend - (e.g userID does not exist)
     * @throws IllegalArgumentException
     *             if the given userID is null.
     * @throws ValidationException
     *             if the entity is not valid.
     */
    Optional<E> addFriend(ID userID, E friend);

    /**
     *
     * @param userID
     *          must be an existing ID
     * @param friend
     *          must be valid
     * @return null - if the friend is removed
     *              otherwise returns the friend - (e.g userID does not exist or the friend isn't in the user's friend list)
     * @throws IllegalArgumentException
     *             if the given userID is null.
     * @throws ValidationException
     *             if the entity is not valid.
     */
    Optional<E> removeFriend(ID userID, ID friend);


    Iterable<F> getFriendships();

}


