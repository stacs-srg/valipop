package uk.ac.standrews.cs.valipop.utils.addressLookup;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ForeignGeography {

    String[] countries = {"Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina",
            "Armenia", "Australia", "Austria", "Azerbaijan", "The Bahamas", "Bahrain", "Bangladesh", "Barbados",
            "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana",
            "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia", "Cameroon", "Canada",
            "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros",
            "Democratic Republic of the Congo", "Republic of the Congo", "Costa Rica", "Côte d’Ivoire", "Croatia",
            "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic",
            "East Timor", "Ecuador", "Egypt", "El Salvador", "England", "Equatorial Guinea", "Eritrea", "Estonia",
            "Ethiopia", "Fiji", "Finland", "France", "Gabon", "The Gambia", "Georgia", "Germany", "Ghana", "Greece",
            "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hungary", "Iceland",
            "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan",
            "Kazakhstan", "Kenya", "Kiribati", "North Korea", "South Korea", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos",
            "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macedonia",
            "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania",
            "Mauritius", "Mexico", "Micronesia, Federated States of", "Moldova", "Monaco", "Mongolia", "Montenegro",
            "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand",
            "Nicaragua", "Niger", "Nigeria", "Northern Ireland", "Norway", "Oman", "Pakistan", "Palau", "Panama",
            "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia",
            "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino",
            "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore",
            "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "Spain", "Sri Lanka", "Sudan",
            "South Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan",
            "Tanzania", "Thailand", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan",
            "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United States", "Uruguay", "Uzbekistan", "Vanuatu",
            "Vatican City", "Venezuela", "Vietnam", "Wales", "Yemen", "Zambia", "Zimbabwe"};

    ArrayList<Address> forignAddresses;
    RandomGenerator randomNumberGenerator;

    public ForeignGeography(RandomGenerator randomNumberGenerator) {

        for(String country : countries) {
            forignAddresses.add(new Address(country));
        }

        this.randomNumberGenerator = randomNumberGenerator;

    }

    public Address getCountry(IPerson person) {
        return forignAddresses.get(randomNumberGenerator.nextInt(forignAddresses.size()));
    }

}
