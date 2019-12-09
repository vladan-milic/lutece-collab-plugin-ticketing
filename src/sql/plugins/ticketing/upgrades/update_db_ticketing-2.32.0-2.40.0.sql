-- attributs pour l'évolution "relance automatique"
ALTER TABLE ticketing_ticket ADD nb_relance int(2) default 0;
ALTER TABLE ticketing_ticket ADD date_derniere_relance timestamp;