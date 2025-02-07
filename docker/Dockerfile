FROM eclipse-temurin:21

WORKDIR /app

# --- Installing latest version of R ---

# update indices
RUN apt update -qq
# install two helper packages we need
RUN apt install -y --no-install-recommends software-properties-common dirmngr
# add the signing key (by Michael Rutter) for these repos
# To verify key, run gpg --show-keys /etc/apt/trusted.gpg.d/cran_ubuntu_key.asc 
# Fingerprint: E298A3A825C0D65DFD57CBB651716619E084DAB9
RUN wget -qO- https://cloud.r-project.org/bin/linux/ubuntu/marutter_pubkey.asc | tee -a /etc/apt/trusted.gpg.d/cran_ubuntu_key.asc
# add the R 4.0 repo from CRAN -- adjust 'focal' to 'groovy' or 'bionic' as needed
RUN add-apt-repository "deb https://cloud.r-project.org/bin/linux/ubuntu $(lsb_release -cs)-cran40/"

RUN apt install -y --no-install-recommends r-base

# For the markdown package in R
RUN apt install -y make gcc build-essential

RUN R -e "install.packages('geepack', repos = c(CRAN = 'https://cloud.r-project.org'))"
