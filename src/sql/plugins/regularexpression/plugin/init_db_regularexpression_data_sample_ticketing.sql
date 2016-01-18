INSERT INTO regularexpression_regular_expression(id_expression, title, regular_expression_value, valid_exemple, information_message, error_message) VALUES
(201, "Compte FacilFamilles", "[0-9]*", "1231545", "<p>Le champ num&eacute;ro de compte ne doit contenir que des chiffres</p>", "Le numéro de compte Facil'Familles saisi ne respecte pas le format attendu. Il ne doit être composé que de chiffres.");

INSERT INTO genatt_verify_by (id_field, id_expression) VALUES
(205, 201), 
(206, 201), 
(207, 201);
