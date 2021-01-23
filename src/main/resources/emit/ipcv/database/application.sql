/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  rinelfi
 * Created: Jan 7, 2021
 */

drop table if exists settings;
drop table if exists translation;
drop table if exists sentence;
drop table if exists language;
drop table if exists recently_project;

create table recently_project(
  proj_location text primary key,
  proj_name text
);

create table language(
  lang_id integer primary key autoincrement,
  lang_code text,
  lang_country text,
  lang_variant text
);

create table sentence(
  phra_original text primary key
);

create table translation(
  tran_id integer primary key autoincrement,
  tran_language integer language,
  tran_sentence text sentence,
  tran_content text,
  foreign key(tran_language) references language(lang_id),
  foreign key(tran_sentence) references sentence(phra_original)
);

create table settings(
  sett_remote_ip_address text,
  sett_remote_port_address integer,
  sett_use_local_hardware text,
  sett_language integer,
  foreign key(sett_language) references language(lang_id)
);

/*
*
*
* Defaults contents
*
*
*/

insert into language(lang_code, lang_country, lang_variant) values
('en', 'US', ''), ('fr', 'FR', '');

insert into sentence(phra_original) values
--a
('all_or_nothing'),
('application_title'),
('apply'),
('average_filtering'),
--b
('background'),
('binarization'),
--c
('cancel'),
('clear_project_history'),
('close'),
('color'),
('confirm'),
('confirm_project_history_deletion_message'),
('confirm_project_history_deletion_title'),
('conservative_smoothing'),
('coordinate'),
('cumulative_histogram'),
--d
('delete'),
('dilation'),
('discrete_covolution'),
('dynamic_display'),
--e
('edition'),
('erosion'),
('erosion_outline'),
('exit'),
('export_image'),
--f
('file'),
('fixed_threshold'),
('fixed_zoom'),
('form'),
('form_recognition'),
--g
('gaussian_filtering'),
('global_preview'),
('graphics'),
--h
('histogram'),
('histogram_equalization'),
('homothety'),
('horizontal_symmetry'),
--i
('invert_grayscale'),
('image_preview'),
('image_size'),
--j
--k
--l
('labeling'),
('local_processing'),
--m
('median_filtering'),
('medium_filtering'),
--n
('new_project'),
('not_registered'),
('not_save'),
('nothing'),
--o
('offset'),
('open'),
('open_an_image'),
('open_project'),
('opening'),
('operation'),
('otsu_threshold'),
--p
('papert_turtle'),
('plane_transformation'),
('point_transformation'),
('processing'),
--q
('quit_application_request_message'),
('quit_application_request_title'),
--r
('recent_project_empty'),
('recents_projects'),
('redo'),
('registered'),
('rotation'),
('rotation_angle'),
--s
('save'),
('save_as'),
('save_changes_request_message'),
('save_changes_request_title'),
('save_image'),
('setting'),
('shape_outline'),
('shear'),
('smoothing'),
('structuring_element'),
('symmetry_in_the_center'),
--t
('thickening'),
('threshold'),
('thresholding'),
('top_hat_closure'),
('top_hat_opening'),
--u
('undo'),
--v
('vertical_symmetry'),
--w
('write_permission_error_message'),
('write_permission_error_title'),
--x
--y
--z
('zoom_in'),
('zoom_out');

/*
*
* English translations
*
*/
insert into translation(tran_language, tran_sentence, tran_content) values
--a
(1, 'all_or_nothing', 'All or nothing'),
(1, 'application_title', 'Image processing and computer vision'),
(1, 'apply', 'Apply'),
(1, 'average_filtering', 'Average filtering'),
--b
(1, 'background', 'Background'),
(1, 'binarization', 'Binarization'),
--c
(1, 'cancel', 'Cancel'),
(1, 'clear_project_history', 'Clear project history'),
(1, 'close', 'Close'),
(1, 'color', 'Color'),
(1, 'confirm', 'Confirm'),
(1, 'confirm_project_history_deletion_message', 'Please confirm deletion from the list of recent projects'),
(1, 'confirm_project_history_deletion_title', 'Delete history'),
(1, 'conservative_smoothing', 'Conservative smoothing'),
(1, 'coordinate', 'Coordinate'),
(1, 'cumulative_histogram', 'Cumulative histogram'),
--d
(1, 'delete', 'Delete'),
(1, 'dilation', 'Dilation'),
(1, 'discrete_covolution', 'Discrete covolution'),
(1, 'dynamic_display', 'Dynamic display'),
--e
(1, 'edition', 'Edition'),
(1, 'erosion', 'Erosion'),
(1, 'erosion_outline', 'Erosion outline'),
(1, 'exit', 'Exit'),
(1, 'export_image', 'Export image as'),
--f
(1, 'file', 'File'),
(1, 'fixed_threshold', 'Fixed threshold'),
(1, 'fixed_zoom', 'Fixed zoom'),
(1, 'form', 'Form'),
(1, 'form_recognition', 'Form recognition'),
--g
(1, 'gaussian_filtering', 'Gaussian filtering'),
(1, 'global_preview', 'Global preview'),
(1, 'graphics', 'Graphics'),
--h
(1, 'histogram', 'Histogram'),
(1, 'histogram_equalization', 'Histogram equalization'),
(1, 'homothety', 'Homothety'),
(1, 'horizontal_symmetry', 'Horizontal symmetry'),
--i
(1, 'invert_grayscale', 'Invert grayscale'),
(1, 'image_preview', 'Image preview'),
(1, 'image_size', 'Image size'),
--j
--k
--l
(1, 'labeling', 'Labeling'),
(1, 'local_processing', 'Local processing'),
--m
(1, 'median_filtering', 'Median filtering'),
(1, 'medium_filtering', 'Medium filtering'),
--n
(1, 'new_project', 'New project'),
(1, 'not_registered', 'Not registered'),
(1, 'not_save', 'Do not save'),
(1, 'nothing', 'Nothing'),
--o
(1, 'offset', 'Offset'),
(1, 'open', 'Open'),
(1, 'open_an_image', 'Open an image'),
(1, 'open_project', 'Open a project'),
(1, 'opening', 'Opening'),
(1, 'operation', 'Operation'),
(1, 'otsu_threshold', 'Otsu thresholding'),
--p
(1, 'papert_turtle', 'Papert turtle'),
(1, 'plane_transformation', 'Plane transformation'),
(1, 'point_transformation', 'Point transformation'),
(1, 'processing', 'Processing'),
--q
(1, 'quit_application_request_message', 'Do you want to quit the app?'),
(1, 'quit_application_request_title', 'Exit the application'),
--r
(1, 'recent_project_empty', 'Recent project empty'),
(1, 'recents_projects', 'Recents projects'),
(1, 'redo', 'Redo'),
(1, 'registered', 'Registered'),
(1, 'rotation', 'Rotation'),
(1, 'rotation_angle', 'Rotation angle'),
--s
(1, 'save', 'Save'),
(1, 'save_as', 'Save as'),
(1, 'save_changes_request_message', 'Do you want to save the changes.[br]Your changes will be lost if you do not save them.'),
(1, 'save_changes_request_title', 'Save changes'),
(1, 'save_image', 'Save image'),
(1, 'setting', 'Setting'),
(1, 'shape_outline', 'Shape outline'),
(1, 'shear', 'Shear'),
(1, 'smoothing', 'Smoothing'),
(1, 'structuring_element', 'Structuring element'),
(1, 'symmetry_in_the_center', 'Symetry in the center'),
--t
(1, 'thickening', 'Thickening'),
(1, 'threshold', 'Threshold'),
(1, 'thresholding', 'Thresholding'),
(1, 'top_hat_closure', 'Top hat (Closure)'),
(1, 'top_hat_opening', 'Top hat (Opening)'),
--u
(1, 'undo', 'Undo'),
--v
(1, 'vertical_symmetry', 'Vertical symetry'),
--w
(1, 'write_permission_error_message', 'You don''t have necessary permission for writing on this location[br]You should execute the application as administrator or change the writing location'),
(1, 'write_permission_error_title', 'Write permission error'),
--x
--y
--z
(1, 'zoom_in', 'Zoom in'),
(1, 'zoom_out', 'Zoom out');
/*
*
* French translations
*
*/
insert into translation(tran_language, tran_sentence, tran_content) values
--a
(2, 'all_or_nothing', 'Tout ou rien'),
(2, 'application_title', 'Traitement d''image et vision par ordinateur'),
(2, 'apply', 'Appliquer'),
(2, 'average_filtering', 'Filtrage moyenne'),
--b
(2, 'background', 'Fond'),
(2, 'binarization', 'Binarisation'),
--c
(2, 'cancel', 'Annuler'),
(2, 'clear_project_history', 'Nettoyer l''historique'),
(2, 'close', 'Fermer'),
(2, 'color', 'Couleur'),
(2, 'confirm', 'Confirmer'),
(2, 'confirm_project_history_deletion_message', 'Veuillez confirmer la suppression de la liste des projets récents'),
(2, 'confirm_project_history_deletion_title', 'Supprimer l''historique'),
(2, 'conservative_smoothing', 'Lissage conservateur'),
(2, 'coordinate', 'Coordonnées'),
(2, 'cumulative_histogram', 'Histogramme cumulé'),
--d
(2, 'delete', 'Supprimer'),
(2, 'dilation', 'Dilatation'),
(2, 'discrete_covolution', 'Convolution discrète'),
(2, 'dynamic_display', 'Étalage de la dynamique'),
--e
(2, 'edition', 'Édition'),
(2, 'erosion', 'Érosion'),
(2, 'erosion_outline', 'Contour par érosion'),
(2, 'exit', 'Quitter'),
(2, 'export_image', 'Exporter l''image sous'),
--f
(2, 'file', 'Fichier'),
(2, 'fixed_threshold', 'Seuillage fixe'),
(2, 'fixed_zoom', 'Zoom fixe'),
(2, 'form', 'Forme'),
(2, 'form_recognition', 'Reconnaissance de forme'),
--g
(2, 'gaussian_filtering', 'Filtrage gaussien'),
(2, 'global_preview', 'Aperçu global'),
(2, 'graphics', 'Graphes'),
--h
(2, 'histogram', 'Histogramme'),
(2, 'histogram_equalization', 'Égalisation d''histogramme'),
(2, 'homothety', 'Homothétie'),
(2, 'horizontal_symmetry', 'Symétrie horizontale'),
--i
(2, 'invert_grayscale', 'Inverser le niveau de gris'),
(2, 'image_preview', 'Aperçue d''image'),
(2, 'image_size', 'Taille de l''image'),
--j
--k
--l
(2, 'labeling', 'Etiquetage'),
(2, 'local_processing', 'Transformation locale'),
--m
(2, 'median_filtering', 'Filtrage médiane'),
(2, 'medium_filtering', 'Filtrage moyen'),
--n
(2, 'new_project', 'Nouveau projet'),
(2, 'not_registered', 'Non enregistré'),
(2, 'not_save', 'Ne pas enregistrer'),
(2, 'nothing', 'Rien'),
--o
(2, 'offset', 'Décallage'),
(2, 'open', 'Ouvrir'),
(2, 'open_an_image', 'Ouvrir une image'),
(2, 'open_project', 'Ouvrir un projet'),
(2, 'opening', 'Ouverture'),
(2, 'operation', 'Opération'),
(2, 'otsu_threshold', 'Seuillage d''Otsu'),
--p
(2, 'papert_turtle', 'Tortue de Papert'),
(2, 'plane_transformation', 'Transformation plane'),
(2, 'point_transformation', 'Transformation ponctuelle'),
(2, 'processing', 'Traitement'),
--q
(2, 'quit_application_request_message', 'Voulez-vous quitter l''application?'),
(2, 'quit_application_request_title', 'Quitter l''application'),
--r
(2, 'recent_project_empty', 'Aucun projet récent'),
(2, 'recents_projects', 'Projets récents'),
(2, 'redo', 'Retablir'),
(2, 'registered', 'Enregistré'),
(2, 'rotation', 'Rotation'),
(2, 'rotation_angle', 'Angle de rotation'),
--s
(2, 'save', 'Enregistrer'),
(2, 'save_as', 'Enregistrer sous'),
(2, 'save_changes_request_message', 'Voulez-vous enregistrer les changements.[br]Vos modifications seront perdues si vous ne les savegardez pas.'),
(2, 'save_changes_request_title', 'Enregistrer les modifications'),
(2, 'save_image', 'Enregistrer l''image'),
(2, 'setting', 'Préférence'),
(2, 'shape_outline', 'Contour de forme'),
(2, 'shear', 'Cisaillement'),
(2, 'smoothing', 'Lissage'),
(2, 'structuring_element', 'Éléments structurants'),
(2, 'symmetry_in_the_center', 'Symétrie au centre'),
--t
(2, 'thickening', 'Épaississement'),
(2, 'threshold', 'Seuil'),
(2, 'thresholding', 'Seuillage'),
(2, 'top_hat_closure', 'Top hat (Férmeture)'),
(2, 'top_hat_opening', 'Top hat (Ouverture)'),
--u
(2, 'undo', 'Annuler'),
--v
(2, 'vertical_symmetry', 'Symétrie verticale'),
--w
(2, 'write_permission_error_message', 'Vous n''avez pas la permission nécessaire pour écrire sur le disque[br]Vous devez éxécuter l''application en tant qu''administrateur ou changer de disque d''écriture'),
(2, 'write_permission_error_title', 'Erreur lors de l''écriture du fichier'),
--x
--y
--z
(2, 'zoom_in', 'Zoom en avant'),
(2, 'zoom_out', 'Zoom en arrière');

insert into settings(sett_language, sett_remote_ip_address, sett_remote_port_address, sett_use_local_hardware) values
                    (1, 'localhost', 2046, 'true');