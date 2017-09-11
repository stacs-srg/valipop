/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.InconsistentWeightException;
import uk.ac.standrews.cs.digitising_scotland.verisim.annotations.names.FileBasedEnumeratedDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.DateUtils;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.MonthDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection.BirthDateSelector;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection.DateSelector;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateSelection.DeathDateSelector;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.EventType;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.birth.NoChildrenOfDesiredOrder;
import uk.ac.standrews.cs.digitising_scotland.verisim.events.death.NotDeadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.EntityFactory;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.Population;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.exceptions.PersonNotFoundException;

import java.io.IOException;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Person implements IPersonExtended {

    private static Logger log = LogManager.getLogger(Person.class);
    public static Random random = new Random();

    private static FileBasedEnumeratedDistribution maleFirstNamesDistribution = null;
    private static FileBasedEnumeratedDistribution femaleFirstNamesDistribution = null;
    private static FileBasedEnumeratedDistribution surnameNamesDistribution = null;

    // TODO
    private static final String maleNames = "proxy-scotland-population-JA/names/female_first_name_probabilities.tsv";
    private static final String femaleNames = "proxy-scotland-population-JA/names/male_first_name_probabilities.tsv";
    private static final String surnames = "proxy-scotland-population-JA/names/surname_probabilities.tsv";

    private static int nextId = 0;
    private int id;
    private char sex;
    private ExactDate birthDate;
    private ExactDate deathDate;
    private List<IPartnershipExtended> partnerships = new ArrayList<>();
    private IPartnershipExtended parentsPartnership = null;
    private String firstName;
    private String surname;

    private DeathDateSelector deathDateSelector = new DeathDateSelector();
    private DateSelector birthDateSelector = new BirthDateSelector();

    private boolean toSeparate = false;

    public Person(char sex, Date birthDate, IPartnershipExtended parentsPartnership) {

        if(maleFirstNamesDistribution == null) {
            try {
                maleFirstNamesDistribution = new FileBasedEnumeratedDistribution(maleNames, random);
                femaleFirstNamesDistribution = new FileBasedEnumeratedDistribution(femaleNames, random);
                surnameNamesDistribution = new FileBasedEnumeratedDistribution(surnames, random);
            } catch (IOException | InconsistentWeightException e) {
                e.printStackTrace();
            }
        }

        id = getNewId();
        this.sex = Character.toLowerCase(sex);
        this.birthDate = birthDate.getExactDate();
        this.parentsPartnership = parentsPartnership;

        if (this.sex == 'm') {
            firstName = maleFirstNamesDistribution.getSample();
        } else {
            firstName = femaleFirstNamesDistribution.getSample();
        }

        if(parentsPartnership == null || parentsPartnership.getMalePartner() == null) {
            surname = surnameNamesDistribution.getSample();
        } else {
            surname = parentsPartnership.getMalePartner().getSurname();
        }

    }

    private static int getNewId() {
        return nextId++;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public char getSex() {
        return sex;
    }

    @Override
    public java.util.Date getBirthDate() {
        return getBirthDate_ex().getDate();
    }

    @Override
    public Date getBirthDate_ex() {
        return birthDate;
    }

    @Override
    public Date getDeathDate_ex() {
        return deathDate;
    }

    @Override
    public List<IPartnershipExtended> getPartnerships_ex() {
        return partnerships;
    }

    @Override
    public IPartnershipExtended getParentsPartnership_ex() {
        return parentsPartnership;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    // TODO Implement geography model
    @Override
    public String getBirthPlace() {
        return null;
    }

    @Override
    public java.util.Date getDeathDate() {
        if(getDeathDate_ex() == null) {
            return null;
        } else {
            return getDeathDate_ex().getDate();
        }
    }

    @Override
    public String getDeathPlace() {
        return null;
    }

    // TODO Implement occupation assignment
    @Override
    public String getOccupation() {
        return null;
    }

    // TODO Implement death causes - does occupation, date, gender, location, etc. influence this?
    @Override
    public String getDeathCause() {
        return null;
    }

    @Override
    public List<Integer> getPartnerships() {

        List<Integer> partnerIDs = new ArrayList<>();

        for(IPartnershipExtended partnership : getPartnerships_ex()) {
            partnerIDs.add(partnership.getId());
        }

        return partnerIDs;
    }

    @Override
    public int getParentsPartnership() {
        if(getParentsPartnership_ex() == null) {
            return -1;
        } else {
            return getParentsPartnership_ex().getId();
        }
    }

    @Override
    public int compareTo(IPersonExtended o) {
        return this.id == o.getId() ? 0 : -1;
    }

    @Override
    public boolean noRecentChildren(MonthDate currentDate, CompoundTimeUnit timePeriod) {

        for (IPartnershipExtended p : getPartnerships_ex()) {
            for (IPersonExtended c : p.getChildren()) {
                if (DateUtils.dateBeforeOrEqual(currentDate.advanceTime(timePeriod), c.getBirthDate_ex())) {
                    return false;
                }
            }
        }

        return true;

    }

    @Override
    public void recordPartnership(IPartnershipExtended partnership) {
        this.partnerships.add(partnership);
    }

    @Override
    public boolean recordDeath(Date date, Population population) {

        if (partnerships.size() != 0) {
            IPersonExtended lastSpouse = getLastChild().getParentsPartnership_ex().getPartnerOf(this);
            if(lastSpouse == null) {
                // TODO remove this once new partnering has been implemented
//                System.out.println("A");
            } else
            if (lastSpouse.aliveOnDate(date)) {
                // if the partner is alive on date of death
                if (lastSpouse.getLastChild().getParentsPartnership_ex().getPartnerOf(lastSpouse).getId() == id) {
                    // and if the lastSpouses last partner is this person
                    // then this is the end of a partnership - caused by death
                    population.getPopulationCounts().partnershipEnd();
                }
            }
        }

        population.getPopulationCounts().death(this);

        deathDate = date.getExactDate();

        return true;
    }

    private boolean isBirthEvent(EventType event) {
        return event == EventType.FIRST_BIRTH || event == EventType.SECOND_BIRTH || event == EventType.THIRD_BIRTH ||
                event == EventType.FOURTH_BIRTH || event == EventType.FIFTH_BIRTH;
    }

    private boolean isDeathEvent(EventType event) {
        return event == EventType.MALE_DEATH || event == EventType.FEMALE_DEATH;
    }

    @Override
    public int ageAtDeath() throws NotDeadException {
        if (deathDate == null) {
            throw new NotDeadException();
        }
        return DateUtils.differenceInYears(birthDate, deathDate).getCount();
    }

    @Override
    public boolean aliveOnDate(Date date) {

        if (DateUtils.dateBeforeOrEqual(birthDate, date)) {
            if (deathDate == null || DateUtils.dateBeforeOrEqual(date, deathDate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int ageAtFirstChild() throws NoChildrenOfDesiredOrder {

        int age = Integer.MAX_VALUE;

        for (IPartnershipExtended p : partnerships) {
            for (IPersonExtended c : p.getChildren()) {
                int ageAtBirth = DateUtils.differenceInYears(birthDate, c.getBirthDate_ex()).getCount();
                if (ageAtBirth < age) {
                    age = ageAtBirth;
                }
            }
        }

        if (age == Integer.MAX_VALUE) {
            throw new NoChildrenOfDesiredOrder("Women has no children");
        }

        return age;
    }

    @Override
    public IPersonExtended getLastChild() {

        Date latestChildBirthDate = new YearDate(Integer.MIN_VALUE);
        IPersonExtended child = null;

        for (IPartnershipExtended p : partnerships) {
            for (IPersonExtended c : p.getChildren()) {

                if(DateUtils.dateBeforeOrEqual(latestChildBirthDate, c.getBirthDate_ex())) {
                    latestChildBirthDate = c.getBirthDate_ex();
                    child = c;
                }

            }
        }

        return child;

    }

    @Override
    public int numberOfChildren() {
        int count = 0;

        for(IPartnershipExtended p : partnerships) {
            count += p.getChildren().size();
        }

        return count;
    }

    @Override
    public void keepFather(PeopleCollection population) {
        Date latestChildBirthDate = new YearDate(Integer.MIN_VALUE);
        IPersonExtended child = null;



        for (IPartnershipExtended p : partnerships) {
            for (IPersonExtended c : p.getChildren()) {

                if(p.getMalePartner() != null && DateUtils.dateBeforeOrEqual(latestChildBirthDate, c.getBirthDate_ex())) {
                    latestChildBirthDate = c.getBirthDate_ex();
                    child = c;
                }

            }
        }

        // This is the partnership with the last father - we're wanting to put any fatherless kids in here
        IPartnershipExtended motherPrevChild = child.getParentsPartnership_ex();


        IPersonExtended newChild = getLastChild();
        IPartnershipExtended old = newChild.getParentsPartnership_ex();

        Collection<IPersonExtended> newChildren = old.getChildren();

        for(IPersonExtended c : newChildren) {
            c.setParentsPartnership(motherPrevChild);

        }

        motherPrevChild.addChildren(newChildren);
        partnerships.remove(old);
        population.removePartnershipFromIndex(old);

    }

    @Override
    public void setParentsPartnership(IPartnershipExtended newParents) {
        parentsPartnership = newParents;
    }

    @Override
    public int numberOfChildrenFatheredChildren() {
        int count = 0;

        for(IPartnershipExtended p : partnerships) {
            if(p.getMalePartner() != null) {
                count += p.getChildren().size();
            }
        }

        return count;
    }

    @Override
    public IPartnershipExtended isInstigatorOfSeparationOfMothersPreviousPartnership() {

        Collection<IPersonExtended> fullSiblings = parentsPartnership.getChildren();
        fullSiblings.remove(this);


        // check to see if eldest sibling
        boolean eldest = true;
        for(IPersonExtended sibling : fullSiblings) {
            if(DateUtils.dateBeforeOrEqual(sibling.getBirthDate_ex(), getBirthDate_ex())) {
                eldest = false;
            }
        }

        IPartnershipExtended prevPartnership = null;

        if(eldest) {
            // is first child of partnership then look to see if there is a previous partnership for the mother
            Collection<IPartnershipExtended> mothersPartnerships = parentsPartnership.getFemalePartner().getPartnerships_ex();
            mothersPartnerships.remove(parentsPartnership);

            for(IPartnershipExtended p : mothersPartnerships) {

                if(DateUtils.dateBeforeOrEqual(p.getPartnershipDate(), parentsPartnership.getPartnershipDate())) {
                    if(prevPartnership != null) {
                        if(DateUtils.dateBeforeOrEqual(prevPartnership.getPartnershipDate(), p.getPartnershipDate())) {
                            prevPartnership = p;
                        }
                    } else {
                        prevPartnership = p;
                    }
                }

            }


        }

        return prevPartnership;
    }

    @Override
    public boolean isWidow(Date onDate) {

        IPersonExtended partner = getPartner(onDate);

        if(partner == null) {
            return false;
        } else {
            return !partner.aliveOnDate(onDate);
        }

    }

    @Override
    public IPersonExtended getPartner(Date onDate) {

        IPartnershipExtended currentPartnership = null;

        for(IPartnershipExtended p : partnerships) {

            if(DateUtils.dateBeforeOrEqual(p.getPartnershipDate(), onDate)) {

                if(currentPartnership != null) {

                    if(DateUtils.dateBeforeOrEqual(currentPartnership.getPartnershipDate(), p.getPartnershipDate())) {
                        currentPartnership = p;
                    }

                } else {
                    currentPartnership = p;
                }

            }

        }

        if(currentPartnership == null) {
            return null;
        } else if(sex == MALE) {
            return currentPartnership.getFemalePartner();
        } else {
            return currentPartnership.getMalePartner();
        }

    }

    @Override
    public void giveChildren(int numberOfChildren, AdvancableDate onDate, CompoundTimeUnit birthTimeStep, Population population) {

        try {
            population.getLivingPeople().removePerson(this);
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        partnerships.add(EntityFactory.formNewChildrenInPartnership(numberOfChildren, this, onDate.getMonthDate(), birthTimeStep, population));

        population.getLivingPeople().addPerson(this);



    }

    @Override
    public void giveChildrenWithinLastPartnership(int numberOfChildren, AdvancableDate onDate, CompoundTimeUnit birthTimeStep, Population population) {

        try {
            population.getLivingPeople().removePerson(this);
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        IPartnershipExtended last = getLastChild().getParentsPartnership_ex();

        Date birthDate = null;

        for(int c = 0; c < numberOfChildren; c++) {
            if(birthDate == null) {
                IPersonExtended child = EntityFactory.makePerson(onDate, birthTimeStep, last, population);
                last.addChildren(Collections.singleton(child));
                birthDate = child.getBirthDate_ex();
            } else {
                IPersonExtended child = EntityFactory.makePerson(onDate, last, population);
                last.addChildren(Collections.singleton(child));
            }

        }

        population.getLivingPeople().addPerson(this);

    }

    @Override
    public boolean toSeparate() {
        return toSeparate;
    }

    @Override
    public void willSeparate(boolean b) {
        toSeparate = b;
    }

    @Override
    public int ageOnDate(Date currentDate) {
        if(birthDate.getDay() == 1 && birthDate.getMonth() == 1) {
            return DateUtils.differenceInYears(birthDate, currentDate).getCount() - 1;
        } else {
            return DateUtils.differenceInYears(birthDate, currentDate).getCount();
        }
    }

    @Override
    public boolean needsNewPartner(AdvancableDate currentDate) {
        return partnerships.size() == 0 || toSeparate() || lastPartnerDied(currentDate);
    }

    private boolean lastPartnerDied(Date currentDate) {
        try {
            return !getLastChild().getParentsPartnership_ex().getMalePartner().aliveOnDate(currentDate);
        } catch (NullPointerException e) {
            return true;
        }
    }

    @Override
    public int numberOfChildrenInLatestPartnership() {
        return getLastChild().getParentsPartnership_ex().getChildren().size();
    }

    @Override
    public Collection<IPersonExtended> getAllChildren() {
        Collection<IPersonExtended> children = new ArrayList<>();

        for(IPartnershipExtended part : getPartnerships_ex()) {
            children.addAll(part.getChildren());
        }

        return children;
    }

    @Override
    public Collection<IPersonExtended> getAllGrandChildren() {
        Collection<IPersonExtended> grandChildren = new ArrayList<>();

        Collection<IPersonExtended> children = getAllChildren();

        for(IPersonExtended c : children) {
            grandChildren.addAll(c.getAllChildren());
        }

        return grandChildren;
    }

    @Override
    public Collection<IPersonExtended> getAllGreatGrandChildren() {
        Collection<IPersonExtended> greatGrandChildren = new ArrayList<>();

        Collection<IPersonExtended> grandChildren = getAllGrandChildren();

        for (IPersonExtended gC: grandChildren) {
            greatGrandChildren.addAll(gC.getAllChildren());
        }

        return greatGrandChildren;
    }

    @Override
    public boolean diedInYear(YearDate year) {
        if(getDeathDate_ex() == null) {
            return false;
        }

        return DateUtils.dateInYear(getDeathDate_ex(), year);
    }

    @Override
    public Collection<IPartnershipExtended> getPartnershipsActiveInYear(YearDate year) {

        Collection<IPartnershipExtended> activePartnerships = new ArrayList<>();

        for(IPartnershipExtended part : getPartnerships_ex()) {
            Date startDate = part.getPartnershipDate();

            if(DateUtils.dateInYear(startDate, year)) {
                activePartnerships.add(part);
            } else {
                for(IPersonExtended p : part.getChildren()) {
                    if(DateUtils.dateInYear(p.getBirthDate_ex(), year)) {
                        activePartnerships.add(part);
                        break;
                    }
                }
            }

        }

        return activePartnerships;
    }

    @Override
    public boolean bornInYear(YearDate year) {
        if(getBirthDate_ex() == null) {
            return false;
        }

        return DateUtils.dateInYear(getBirthDate_ex(), year);
    }

    @Override
    public boolean aliveInYear(YearDate y) {
        return bornInYear(y) || diedInYear(y) || aliveOnDate(y);
    }

    @Override
    public IPartnershipExtended getLastPartnership() {

        Date latestPartnershipDate = new YearDate(Integer.MIN_VALUE);
        IPartnershipExtended partnership = null;

        for (IPartnershipExtended p : partnerships) {
            if(DateUtils.dateBefore(latestPartnershipDate, p.getPartnershipDate())) {
                latestPartnershipDate = p.getPartnershipDate();
                partnership = p;
            }
        }
        return partnership;
    }

    @Override
    public Integer numberOfChildrenBirthedBeforeDate(YearDate y) {

        int count = 0;

        for(IPartnershipExtended p : getPartnerships_ex()) {
            for(IPersonExtended c : p.getChildren()) {
                if(DateUtils.dateBefore(c.getBirthDate_ex(), y)) {
                    count ++;
                }
            }
        }

        return count;
    }

    @Override
    public boolean bornBefore(Date date) {
        return DateUtils.dateBefore(getBirthDate_ex(), date);
    }

    @Override
    public boolean bornOnDate(Date y) {
        return DateUtils.datesEqual(y, birthDate);
    }

}
