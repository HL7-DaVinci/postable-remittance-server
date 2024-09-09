package org.hl7.davinci.pr.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hl7.davinci.pr.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ClaimQueryDaoImpl implements ClaimQueryDao {

    public static final String PATIENT_JOIN = "PATIENT";
    public static final String CLAIM_QUERY_JOIN = "CLAIM_QUERY";
    public static final String PAYER_JOIN = "PAYER";
    public static final String PAYMENT_JOIN = "PAYMENT";
    public static final String REMITTANCE_JOIN = "REMITTANCE";
    public static final Character WILD_CARD_CHAR = '%';
    @Autowired
    private EntityManager entityManager;

    /**
     * @param tin              - required
     * @param patientDob       - required
     * @param dosStart
     * @param dosEnd
     * @param patientId
     * @param payerIdentity
     * @param payerName
     * @param patientFirstName
     * @param patientLastName
     * @return {@link List<Tuple>} with {@link Tuple} of entities in the order: {@link ClaimQuery},
     * * {@link Patient}, {@link Payer}, {@link Payment}, {@link Remittance}
     * * <p>
     * * If there are no claims found for the provided values no records will be returned
     */
    @Override
    public List<Tuple> findByPatient(String tin, Date patientDob, Date dosStart, Date dosEnd, String patientId, String payerIdentity, String payerName, String patientFirstName, String patientLastName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleQuery = criteriaBuilder.createTupleQuery();

        Root<ClaimQuery> rootClaim = tupleQuery.from(ClaimQuery.class);

        Specification<ClaimQuery> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Map<String, Selection<?>> selectionMap = buildSelection(root, true, true);
            List<Selection<?>> selections = new ArrayList<>(selectionMap.values());
            //all common fields are built here
            buildCommonCriteria(tin, dosStart, dosEnd, payerIdentity, payerName, root, query, cb, predicates, selectionMap);

            //required params
            RepoUtils.addEqualFilter(ClaimQuery_.SUBSCRIBER_PATIENT_ID, patientId, root, null, cb, predicates, true);
            Join<Object, Object> patientJoin = (Join<Object, Object>) selectionMap.get(PATIENT_JOIN);

            RepoUtils.addEqualFilter(Patient_.DATE_OF_BIRTH, patientDob, null, patientJoin, cb, predicates, true);
            RepoUtils.addEqualFilter(Patient_.FIRST_NAME, patientFirstName, null, patientJoin, cb, predicates, false);
            RepoUtils.addEqualFilter(Patient_.LAST_NAME, patientLastName, null, patientJoin, cb, predicates, false);

            query.where(predicates.toArray(new Predicate[0]));
            query.multiselect(selections);
            return query.getRestriction();
        };

        Predicate predicate = specification.toPredicate(rootClaim, tupleQuery, criteriaBuilder);
        tupleQuery.where(predicate);

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(tupleQuery);
        //Tuple will have all joined entities in the order they are added
        List<Tuple> result = typedQuery.getResultList();
        return result;
    }

    /**
     * Find payment
     *
     * @param tin                   - required
     * @param paymentNumber         - required
     * @param paymentIssueDateStart - required
     * @param paymentIssueDateEnd   - required
     * @param dosStart
     * @param dosEnd
     * @param payerIdentity
     * @param payerName
     * @param paymentAmountLow
     * @param paymentAmountHigh
     * @return {@link List<Tuple>} with {@link Tuple} of entities in the order: {@link ClaimQuery}, {@link Payer}, {@link Payment}, {@link Remittance}
     * If no payment found based on required values, we'll return null for payment, but will include claim, payer and remittance
     */
    @Override
    public List<Tuple> findByPayment(String tin, String paymentNumber, Date paymentIssueDateStart, Date paymentIssueDateEnd,
                                     Date dosStart, Date dosEnd, String payerIdentity, String payerName, Float paymentAmountLow, Float paymentAmountHigh) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleQuery = criteriaBuilder.createTupleQuery();

        Root<ClaimQuery> rootClaim = tupleQuery.from(ClaimQuery.class);

        Specification<ClaimQuery> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Map<String, Selection<?>> selectionMap = buildSelection(root, false, true);
            List<Selection<?>> selections = new ArrayList<>(selectionMap.values());
            //all common fields are built here
            buildCommonCriteria(tin, dosStart, dosEnd, payerIdentity, payerName, root, query, cb, predicates, selectionMap);

            //payment specific params
            Join<Object, Object> paymentJoin = (Join<Object, Object>) selectionMap.get(PAYMENT_JOIN);

            //required
            Predicate predicateIssueDtPred = cb.between(paymentJoin.get(Payment_.PAYMENT_ISSUE_DT), paymentIssueDateStart, paymentIssueDateEnd);
            predicates.add(predicateIssueDtPred);
            //add wildcard char and lowercase it for paymentnumber
            String paymentNumberForSearch = WILD_CARD_CHAR + paymentNumber.trim().toLowerCase() + WILD_CARD_CHAR;
            Predicate paymNumPred = cb.like(cb.lower(paymentJoin.get(Payment_.PAYMENT_NUMBER)), paymentNumberForSearch);
            predicates.add(paymNumPred);

            //optional
            if (paymentAmountLow != null && paymentAmountHigh != null) {
                Predicate paymAmtPred = cb.between(paymentJoin.get(Payment_.AMOUNT), paymentAmountLow, paymentAmountHigh);
                predicates.add(paymAmtPred);
            }
            query.where(predicates.toArray(new Predicate[0]));
            query.multiselect(selections);
            return query.getRestriction();
        };
        Predicate predicate = specification.toPredicate(rootClaim, tupleQuery, criteriaBuilder);
        tupleQuery.where(predicate);

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(tupleQuery);
        //Tuple will have all joined entities in the order they are added
        List<Tuple> result = typedQuery.getResultList();
        return result;
    }

    /**
     * @param remittanceAdviceId
     * @return {@link List<Tuple>} with {@link Tuple} of entities in the order: {@link ClaimQuery},
     *         {@link Patient}, {@link Payer}, {@link Payment}, {@link Remittance}
     *
     * If there are no remittances found for the provided values no records will be returned
     */
    @Override
    public List<Tuple> findByRemittance(String remittanceAdviceId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleQuery = criteriaBuilder.createTupleQuery();
        Root<ClaimQuery> rootClaim = tupleQuery.from(ClaimQuery.class);

        Specification<ClaimQuery> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Map<String, Selection<?>> selectionMap = buildSelection(root, true, false);
            List<Selection<?>> selections = new ArrayList<>(selectionMap.values());
            Join<Object, Object> remittanceJoin = (Join<Object, Object>) selectionMap.get(REMITTANCE_JOIN);

            //required
            Predicate remittanceAdviceIdPred = cb.equal(remittanceJoin.get(Remittance_.REMITTANCE_ADVICE_ID), remittanceAdviceId);
            predicates.add(remittanceAdviceIdPred);

            query.where(predicates.toArray(new Predicate[0]));
            query.multiselect(selections);
            return query.getRestriction();
        };

        Predicate predicate = specification.toPredicate(rootClaim, tupleQuery, criteriaBuilder);
        tupleQuery.where(predicate);

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(tupleQuery);
        //Tuple will have all joined entities in the order they are added
        List<Tuple> result = typedQuery.getResultList();
        return result;
    }


    /**
     * @param tin               - provider practice tin - required
     * @param providerClaimId   - claim ID as assigned by provider - required
     * @param dosStart          - date of Service Start
     * @param dosEnd            - date of Service End
     * @param patientId         - subscriber/patientID assigned by payer
     * @param payerIdentity
     * @param payerName
     * @param payerClaimId      - claim ID generated by payer
     * @param providerId        - claim ID generated by payer
     * @param claimChargeAmount
     * @return {@link List<Tuple>} with {@link Tuple} of entities in the order: {@link ClaimQuery},
     * {@link Patient}, {@link Payer}, {@link Payment}, {@link Remittance}
     *
     * If there are no claims found for the provided values no records will be returned
     **/
    @Override
    public List<Tuple> findByClaim(String tin, String providerClaimId, Date dosStart, Date dosEnd,
                                   String patientId, String payerIdentity, String payerName,
                                   String payerClaimId, String providerId, Float claimChargeAmount) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleQuery = criteriaBuilder.createTupleQuery();

        Root<ClaimQuery> rootClaim = tupleQuery.from(ClaimQuery.class);

        Specification<ClaimQuery> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Map<String, Selection<?>> selectionMap = buildSelection(root, true, true);
            List<Selection<?>> selections = new ArrayList<>(selectionMap.values());

            buildCommonCriteria(tin, dosStart, dosEnd, payerIdentity, payerName, root, query, cb, predicates, selectionMap);

            //claim specific
            //required
            Predicate providerClaimIdPred = cb.equal(root.get(ClaimQuery_.PROVIDER_CLAIM_ID), providerClaimId);
            predicates.add(providerClaimIdPred);
            //optional parameters
            RepoUtils.addEqualFilter(ClaimQuery_.SUBSCRIBER_PATIENT_ID, patientId, root, null, cb, predicates, false);
            RepoUtils.addEqualFilter(ClaimQuery_.PROVIDER_NP_I, providerId, root, null, cb, predicates, false);
            RepoUtils.addEqualFilter(ClaimQuery_.PAYER_CLAIM_ID, payerClaimId, root, null, cb, predicates, false);
            RepoUtils.addEqualFilter(ClaimQuery_.CLAIM_CHARGE_AMOUNT, claimChargeAmount, root, null, cb, predicates, false);

            query.where(predicates.toArray(new Predicate[0]));
            query.multiselect(selections);
            return query.getRestriction();
        };

        Predicate predicate = specification.toPredicate(rootClaim, tupleQuery, criteriaBuilder);
        tupleQuery.where(predicate);

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(tupleQuery);
        //Tuple will have all joined entities in the order they are added
        List<Tuple> result = typedQuery.getResultList();
        return result;
    }

    private static void buildCommonCriteria(String tin, Date dosStart, Date dosEnd,
                                            String payerIdentity,
                                            String payerName,
                                            Root<ClaimQuery> root, CriteriaQuery<?> query,
                                            CriteriaBuilder cb,
                                            List<Predicate> predicates,
                                            Map<String, Selection<?>> selectionMap) {
        //same params across all searches
        //claim_query fields
        RepoUtils.addEqualFilter(ClaimQuery_.PROVIDER_TIN, tin, root, null, cb, predicates, true);
        //TBD might change if it's a range
        Join<Object, Object> payerJoin = (Join<Object, Object>) selectionMap.get(PAYER_JOIN);

        //optional on payer
        RepoUtils.addEqualFilter(Payer_.PAYER_NAME, payerName, null, payerJoin, cb, predicates, false);
        RepoUtils.addEqualFilter(Payer_.PAYER_IDENTITY, payerIdentity, null, payerJoin, cb, predicates, false);

        if (dosStart != null && dosEnd != null) {
            Predicate predicateDos = cb.between(root.get(ClaimQuery_.DATE_OF_SERVICE), dosStart, dosEnd);
            predicates.add(predicateDos);
        }
    }

    private static Map<String, Selection<?>> buildSelection(Root<ClaimQuery> root, boolean includePatient, boolean isOptionalRemittance) {
        //we need data returned from those no matter if they are searched by
        //always needs to have a patient or a payer, so inner join for them
        Map<String, Selection<?>> selectionMap = new LinkedHashMap<>();
        selectionMap.put(CLAIM_QUERY_JOIN, root);

        if (includePatient) {
            Join<Object, Object> patientJoin = root.join(ClaimQuery_.PATIENT, JoinType.LEFT);
            selectionMap.put(PATIENT_JOIN, patientJoin);
        }
        Join<Object, Object> payerJoin = root.join(ClaimQuery_.PAYER, JoinType.LEFT);
        //if there is payer it should have identity so inner join

        //0 to * -> left joins for payment and remittance
        Join<Object, Object> paymentJoin = root.join(ClaimQuery_.PAYMENT, JoinType.LEFT);

        JoinType remitJoinType = (isOptionalRemittance)?JoinType.LEFT:JoinType.INNER;
        Join<Object, Object> remittanceJoin = root.join(ClaimQuery_.REMITTANCES, remitJoinType);

        selectionMap.put(PAYER_JOIN, payerJoin);
        selectionMap.put(PAYMENT_JOIN, paymentJoin);
        selectionMap.put(REMITTANCE_JOIN, remittanceJoin);

        return selectionMap;
    }


}
