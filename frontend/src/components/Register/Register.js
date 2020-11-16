import React, { Component } from "react";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";
import CssBaseline from "@material-ui/core/CssBaseline";
import TextField from "@material-ui/core/TextField";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import Grid from "@material-ui/core/Grid";
import CreateIcon from "@material-ui/icons/Create";
import Typography from "@material-ui/core/Typography";
import {  withStyles } from "@material-ui/core/styles";
import Container from "@material-ui/core/Container";
import colors from "../../utils/colors";
import Autocomplete from "@material-ui/lab/Autocomplete";
import MuiAlert from "@material-ui/lab/Alert";
import Snackbar from "@material-ui/core/Snackbar";

const StyledTextField = withStyles({
  root: {
    "& .MuiInputBase-input": {
      color: colors.secondary,
    },
    "& .Mui-required": {
      color: colors.primaryLight,
    },
    "& label.Mui-focused": {
      color: colors.tertiary,
    },
    "& .MuiInput-underline:after": {
      borderBottomColor: colors.tertiary,
    },
    "& .MuiOutlinedInput-root": {
      "& fieldset": {
        borderColor: colors.secondaryLight,
      },
      "&:hover fieldset": {
        borderColor: colors.secondaryDark,
      },
      "&.Mui-focused fieldset": {
        borderColor: colors.tertiary,
      },
    },
  },
})(TextField);

const StyledButton = withStyles({
  root: {
    background: colors.tertiary,
    color: colors.secondary,
    "&:hover": {
      backgroundColor: colors.tertiaryDark,
    },
  },
})(Button);

const StyledFormControlLabel = withStyles({
  root: {
    color: colors.tertiary,
    "& .MuiFormControlLabel-label	": {
      color: colors.secondary,
    },
    "&$checked": {
      color: colors.quinary,
    },
  },
})(FormControlLabel);

const StyledCheckbox = withStyles({
  root: {
    color: colors.tertiary,
    "&$checked": {
      color: colors.quaternary,
    },
  },
  checked: {},
})(Checkbox);

const useStyles = (theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },
  typography: {
    color: colors.secondary,
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: colors.tertiary,
    color: colors.secondary,
  },
  form: {
    width: "100%", // Fix IE 11 issue.
    marginTop: theme.spacing(3),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
});

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

class Register extends Component {
  constructor(props) {
    super(props);
    this.state = {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      researchAreas: [],
      checkbox: false,
      showError: false,
      showSuccess: false,
      fieldEmptyError: false,
    };
  }
  handleCheck = (event) => {
    this.setState({ checkbox: event.target.checked });
  }
  handleCloseFieldEmpty = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ fieldEmptyError: false });
  };

  handleCloseError = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ showError: false });
  };

  handleCloseSuccess = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    this.setState({ showSuccess: false });
  };

  handleSubmit = () => {
    if (
      this.state.firstName === "" ||
      this.state.lastName === "" ||
      this.state.email === "" ||
      this.state.password === "" ||
      this.state.researchAreas.length === 0 ||
      !this.state.checkbox
    ) {
      this.setState({ fieldEmptyError: "Fields are required" });
      return;
    }
    const url = "https://react-my-burger-78df4.firebaseio.com/register.json";
    const data = {
      firstName: this.state.firstName,
      lastName: this.state.lastName,
      email: this.state.email,
      password: this.state.password,
      researchAreas: this.state.researchAreas,
    };
    fetch(url, {
      method: "POST",
      body: JSON.stringify(data),
    })
      .then((response) => {
        if (response.status === 200) {
          this.setState({
            showSuccess: "Successfully registered. Please login to continue.",
          });
          this.setState({
            firstName: "",
            lastName: "",
            email: "",
            password: "",
            researchAreas: [],
            checkbox: false,
          });
        }
        return response.json();
      })
      .catch((err) => {
        this.setState({ showError: "Error occured. Check your credientials." });
        console.log(err);
      });
  };

  handleKeyDown = (event) => {
    switch (event.key) {
      case ",": {
        event.preventDefault();
        event.stopPropagation();
        if (event.target.value.length > 0) {
          this.setState({
            researchAreas: [...this.state.researchAreas, event.target.value],
          });
        }
        break;
      }
      default:
    }
  };

  render() {
    const { classes } = this.props;
    return (
      <Container component="main" maxWidth="xs">
        <CssBaseline />
        <div className={classes.paper}>
          <Avatar className={classes.avatar}>
            <CreateIcon />
          </Avatar>
          <Typography
            component="h1"
            variant="h5"
            className={classes.typography}
          >
            Register
          </Typography>

          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <StyledTextField
                margin="normal"
                name="firstName"
                variant="outlined"
                required
                fullWidth
                id="firstName"
                label="First Name"
                autoFocus
                value={this.state.firstName}
                onChange={(e) => this.setState({ firstName: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <StyledTextField
                margin="normal"
                variant="outlined"
                required
                fullWidth
                id="lastName"
                label="Last Name"
                name="lastName"
                value={this.state.lastName}
                onChange={(e) => this.setState({ lastName: e.target.value })}
              />
            </Grid>
          </Grid>

          <Grid item xs={12}>
            <StyledTextField
              variant="outlined"
              required
              fullWidth
              id="email"
              margin="normal"
              label="Email Address"
              name="email"
              value={this.state.email}
              onChange={(e) => this.setState({ email: e.target.value })}
            />
            <StyledTextField
              variant="outlined"
              required
              fullWidth
              margin="normal"
              name="password"
              label="Password"
              type="password"
              id="password"
              value={this.state.password}
              onChange={(e) => this.setState({ password: e.target.value })}
            />
            <Autocomplete
              multiple
              freeSolo
              id="tags-outlined"
              options={researchAreaList}
              getOptionLabel={(option) => option.title || option}
              value={this.state.researchAreas}
              onChange={(e, newValue) =>
                this.setState({ researchAreas: [...newValue] })
              }
              filterSelectedOptions
              renderInput={(params) => {
                params.inputProps.onKeyDown = this.handleKeyDown;
                return (
                  <StyledTextField
                    {...params}
                    margin="normal"
                    variant="outlined"
                    required
                    name="researchAreas"
                    label="Research Areas"
                    placeholder="Use ',' as a delimeter"
                    id="researchAreas"
                    fullWidth
                  />
                );
              }}
            />
          </Grid>

          <StyledFormControlLabel
            control={<StyledCheckbox checked={this.state.checkbox} onChange={this.handleCheck} required value="allowExtraEmails" />}
            label="I accept the terms and conditions"

          />

          <StyledButton
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            className={classes.submit}
            onClick={this.handleSubmit}
          >
            Register
          </StyledButton>
          {this.state.fieldEmptyError && (
            <Snackbar
              open={this.state.fieldEmptyError}
              autoHideDuration={3000}
              onClose={this.handleCloseFieldEmpty}
            >
              <Alert
                style={{ backgroundColor: colors.quinary }}
                severity="error"
                onClose={this.handleCloseFieldEmpty}
              >
                {this.props.fieldEmptyError || this.state.fieldEmptyError}
              </Alert>
            </Snackbar>
          )}
          {this.state.showError && (
            <Snackbar
              open={this.state.showError}
              autoHideDuration={3000}
              onClose={this.handleCloseError}
            >
              <Alert
                style={{ backgroundColor: colors.quinary }}
                severity="error"
                onClose={this.handleCloseError}
              >
                {this.props.showError || this.state.showError}
              </Alert>
            </Snackbar>
          )}
          {this.state.showSuccess && (
            <Snackbar
              open={this.state.showSuccess}
              autoHideDuration={3000}
              onClose={this.handleCloseSuccess}
            >
              <Alert
                style={{ backgroundColor: colors.quaternary }}
                severity="success"
                onClose={this.handleCloseSuccess}
              >
                {this.props.showSuccess || this.state.showSuccess}
              </Alert>
            </Snackbar>
          )}
        </div>
      </Container>
    );
  }
}

const researchAreaList = [
  "3D Integrated Circuits",
  "4D Printing",
  "5G",
  "6G",
  "Accounting & Financing",
  "Activism",
  "Activism on Campus",
  "Adaptive Technologies",
  "Addictions",
  "Additive Manufacturing",
  "Adolescent Psychiatry",
  "Adult Education",
  "Aerospace Engineering",
  "African American Studies",
  "Aging Workforce",
  "Agricultural & Food Technologies",
  "Agricultural Technologies",
  "AI & Mental Health",
  "Air Taxis",
  "Algorithms",
  "Alternative Funding Models",
  "Alternative Pain Management",
  "Animal Rights",
  "Animal Rights",
  "Anthropology & Archaeology",
  "Anxiety Treatment & Care",
  "Aquaculture",
  "Architecture",
  "Archival Management",
  "Archival Preservation",
  "Archival Studies",
  "Art & Design",
  "Artificial Intelligence",
  "Artificial Intelligence",
  "Artificial Intelligence in Libraries",
  "Assistive Technologies",
  "Assistive Technologies",
  "Astronomy",
  "Astronomy Computing",
  "Augmented Airports",
  "Authentication Technology",
  "Autoimmune Disorders",
  "Automated Business",
  "Automated Driving & Connected Vehicles",
  "Automated Voice Spam Protection",
  "Autonomous Things",
  "Autonomous Vehicle Regulations",
  "Aviation Engineering",
  "Aviation Systems",
  "Banned Literature",
  "Bio-Chips",
  "Bio-Printing",
  "Biocrime and Biosecurity",
  "Biodiversity",
  "Bioinformatics",
  "Biological Anthropology",
  "Biology",
  "Biomedical Technologies",
  "Biometrics",
  "Black Market",
  "Blockchain Technology",
  "Blogs and Blogging",
  "Brain-Computer Interface",
  "Business & Organizational Research",
  "Business Education",
  "Business Ethics & Law",
  "Business Information Systems",
  "Business Intelligence",
  "Cancel Culture",
  "Cannabis Industry",
  "Cannabis Studies",
  "Cannabis Studies",
  "Carbon Dioxide Catchers",
  "Career Pathways",
  "Charter Schools",
  "Chatbots",
  "Chemical Engineering",
  "Chemical Weapons",
  "Chemical Weapons",
  "Chemistry",
  "Chemistry",
  "Child Abuse Reporting",
  "Childhood Care",
  "Civic Engagement",
  "Civic Engagement",
  "Civil Engineering",
  "Classroom Design",
  "CleanOS",
  "Climate Change",
  "Clinical Science",
  "Cloud Computing",
  "Cloud-Based Services",
  "Cognitive Government",
  "Cognitive Informatics",
  "College Accessibility",
  "Communications Theory",
  "Community Engagement",
  "Community-School Partnerships",
  "Complementary & Alternative Medicine",
  "Computational Biology & Bioinformatics",
  "Computational Biology & Bioinformatics",
  "Computational Intelligence",
  "Computer Engineering",
  "Computer Graphics & Art",
  "Computer Science Education",
  "Computer Vision",
  "Connected Home",
  "Connected Learning",
  "Conservation",
  "Consulting & Mentorship",
  "Consumer Anxiety",
  "Criminal Science & Forensics",
  "Criminology",
  "Crisis Communications",
  "Crisis Response & Management",
  "CRISPR Research",
  "Cross-Government",
  "Crowdfunding Platforms",
  "Crowdfunding Platforms",
  "Crowdsourced Crime Prevention",
  "Cryptocurrencies",
  "Cryptography",
  "Cultural Competence",
  "Cultural Storytelling",
  "Cultured Meat Processing",
  "Customer-Centric Human Services",
  "Cyber & Network Security",
  "Cyber Behavior",
  "Cyber Physical Systems",
  "Cyberbullying & Cyberharassment",
  "Cybernetics",
  "Cyberpsychology",
  "Cyberterrorism",
  "Cyberwarfare",
  "Dark Web & Deep Web",
  "Data Breaches",
  "Data Encryption",
  "Data Privacy Legislation",
  "Data Visualization",
  "Data-Smart Government",
  "Databases & Data Analysis",
  "Decarbonization Engineering",
  "Decision Support Systems",
  "Deep Learning",
  "Deep Reinforcement Learning",
  "Deep Sea Mining",
  "Deep Sea Mining",
  "Defense and Military Ethics",
  "Demographic Pressure on Economy",
  "Dentistry",
  "Depression and Anxiety During Pregnancy",
  "Digital Communications",
  "Digital Humanities",
  "Digital Libraries",
  "Digital Media",
  "Digital Twin",
  "Digitization of Services",
  "Disaster Management & Preparedness",
  "Disaster Relief & Management",
  "Disruptive Technology",
  "Disruptive Technology",
  "Distributed Governance",
  "Distributed Systems",
  "DNA Digital Storage",
  "Documentary Filmmaking",
  "Domestic Abuse",
  "Drones",
  "Drones",
  "Drug Trafficking",
  "Drug Use in Libraries",
  "E-Books and Audiobooks",
  "E-Commerce",
  "Earth Science",
  "Economic Interconnectedness",
  "Economics & Economic Theory",
  "Edge Computing",
  "Educational Administration & Leadership",
  "Educational Marketing",
  "Election Monitoring/Security",
  "Electrical Engineering",
  "Electronic Government",
  "Embedded Systems",
  "Emergency Medicine",
  "Emerging Market Economies",
  "End-User Computing",
  "Energy Grids",
  "Engineering Education",
  "Engineering Science",
  "Entomophagy",
  "Entrepreneurship",
  "Environmental & Agricultural Informatics",
  "Environmental Consulting Services",
  "Environmental Engineering",
  "Environmental Monitoring & Sensors",
  "Environmental Policies",
  "Environmental Science",
  "Environmental Technologies",
  "Environmentally Friendly Production",
  "Equality in Education",
  "Equity of Access",
  "eSports",
  "eSports Industry",
  "Ethics & Social Responsibility",
  "Ethics and Governance of AI",
  "Ethnic Conflict",
  "Facial Recognition",
  "False Information",
  "Fashion Studies",
  "Feminism",
  "Feminist Film Theory",
  "Flatter Organizations",
  "Flexible Mobile Devices",
  "Flexible Seating Classrooms",
  "Food Production",
  "Food Safety",
  "Food Security",
  "Forensic Biology",
  "Freedom of Speech",
  "Funding",
  "Fusion Power",
  "Future of Librarian Roles",
  "Game-Based Learning",
  "Gaming",
  "Gender & Diversity",
  "Gender & Diversity",
  "Gender Discrimination",
  "Gender Discrimination",
  "Gender Diversity",
  "Gender Economics & Consumption",
  "Genealogy",
  "Generation Alpha Studies",
  "Generation Z Studies",
  "Genetic Engineering",
  "Genetic Engineering",
  "Genetic Testing",
  "Genetically Modified Organism (GMOs)",
  "Genetics & Genomics",
  "Genome Sequencing",
  "Geographic Borders & Conflicts",
  "Geriatric Care",
  "GIS & Geospatial Technology",
  "Global Branding",
  "Global Business",
  "Global Citizens",
  "Global Information Technology",
  "Global Labor Standards",
  "Global Mobility",
  "Government Inclusiveness",
  "Government Intervention in Business",
  "Government Regulations",
  "Government Technology Ethics",
  "Government Transparency",
  "Grid Computing",
  "Hacking & Hacktivism",
  "Hate Crimes",
  "Hate Crimes & Hate Speech",
  "Health Information Systems",
  "Health Psychology",
  "Health Technology Use",
  "Healthcare Administration",
  "Healthcare Policy & Reform",
  "High Performance Computing",
  "Higher Education",
  "Holocaust Cinema",
  "Holography-Assisted Surgery",
  "Home Schooling",
  "Homeland Secuity",
  "Hospitality, Travel, & Tourism Management",
  "Human Augmentation",
  "Human Computer Interaction",
  "Human Resources Development",
  "Human Trafficking",
  "Human Trafficking in the Hospitality Industry",
  "Hydrology",
  "Hyper Local Social Media",
  "Hyperconnected Citizens vs. Barely Connected",
  "Hyperconnectivity",
  "Identity Appeals",
  "Immersive Technologies",
  "Immigrant & Refugee Education",
  "Immigration & Refugees",
  "Industrial Engineering",
  "Information Resources Management",
  "Information Retrieval",
  "Information Security",
  "Instructional Design",
  "Intellectual Freedom",
  "Intelligent Automation",
  "Intelligent Automation",
  "Interactive Technologies",
  "Internal Communications",
  "International Conflict and Negotiation",
  "International Importing/Exporting Regulations",
  "International Journalist Protection",
  "International Law & Lawmaking",
  "International Trade Policies",
  "Internet Censorship Tools",
  "Internet Law",
  "Internet of Machines",
  "Internet of Things",
  "Internet Safety",
  "Internet, Data, & Social Media Privacy",
  "IT Policy & Standardization",
  "IT Research & Theory",
  "IT Security & Ethics",
  "Japanese Popular Culture",
  "Journalism",
  "K-12 Distance Learning",
  "K-12 Education",
  "Knowledge Discovery",
  "Knowledge Management",
  "Lab On a Chip",
  "Lab-Grown Products",
  "Lab-Grown Products",
  "Land Transportation",
  "Law",
  "Learning Assessment & Measurement",
  "Learning Disabilities",
  "LGBTQ+ Studies",
  "Library Administration",
  "Library Apps",
  "Library Information Systems",
  "Library Management",
  "Library Programs",
  "Library Science",
  "Library Training",
  "Lifelong Learning in the Workforce",
  "Linguistics",
  "Local Government",
  "Machine Learning",
  "Macroscope Systems",
  "Management Science",
  "Manufacturing",
  "Marketing",
  "Marriage & Health",
  "Mass Communications",
  "Mass Communications Law",
  "Mass Customization",
  "Mass Customization",
  "Materials Science",
  "Maternal Health",
  "Mathematics",
  "Mechanical Engineering",
  "Media Bias",
  "Media Consumption",
  "Media Fandom",
  "Media Representation",
  "Medical Diagnosis & Treatment",
  "Medical Education",
  "Medical Engineering",
  "Medical Ethics",
  "Medical Screening Tests",
  "Medical Technologies",
  "Megacities",
  "Micro-Enterprises",
  "Micro-Entrepreneurship",
  "Military Technology",
  "Military Technology",
  "Millennials",
  "Mining Engineering",
  "Misinformation",
  "Mobile & Wireless Computing",
  "Mobile Apps",
  "Mobile Apps",
  "Mobile Computing",
  "Mobile Devices in Education",
  "Mobile Devices in Healthcare",
  "Mobile Libraries",
  "Mobile Network Security",
  "Mobile Payment Systems",
  "Mobile Payment Systems",
  "Mobile Wallets",
  "Montessori Education",
  "Multicore",
  "Multicultural Instruction",
  "Multimedia Technology",
  "Munitions",
  "Museums as Learning Hubs",
  "Music",
  "Nanotechnology",
  "National Security",
  "Natural Language Processing",
  "Negotiation Tactics",
  "Net Zero Emissions Policies",
  "Network Architecture",
  "Networking & Telecommunications",
  "Neurodiversity",
  "Neuromorphic Computing",
  "Neuromorphic Engineering",
  "Neuromorphic Engineering",
  "Neuroscience",
  "New Media",
  "Next-Generation Apprenticeships",
  "Nuclear Science",
  "Nuclear Science",
  "Nursing",
  "Nursing Home/Long-Term Care Facility Ethics",
  "Nutrigenomics",
  "Nutrition & Food Science",
  "Obesity",
  "Obsessive Compulsive Disorder Research",
  "Off-Grid Living",
  "Open Government",
  "Open Intellectual Property Movement",
  "Open Source Software",
  "Operations & Service Management",
  "Optical Engineering",
  "Optometry/Ophthalmology",
  "Organ Donors",
  "Organizational Behavior",
  "Out of School Learning",
  "Overfishing/Fish Farming",
  "Palliative Care",
  "Peer-to-Peer Learning",
  "Peer-to-Peer Networks",
  "Penology & Corrections",
  "Personalized Learning",
  "Personalized Medicine",
  "Petroleum & Gas Engineering",
  "Pharmaceutical Sciences",
  "Pharmacology",
  "Pharmacy Ethics",
  "Philosophy",
  "Photonics",
  "Physician Ethics",
  "Physics",
  "Play/Recess",
  "Police Science",
  "Political Correctness",
  "Political Science",
  "Postpartum Recovery",
  "Predictive Analytics",
  "Preservice & Inservice Teaching",
  "Preventative Health Care",
  "Product Development",
  "Professionalism",
  "Programming",
  "Propaganda & Misinformation",
  "Psychiatry & Mental Health",
  "Psychology",
  "Psychology-Influenced Policy",
  "Public & Sector Management",
  "Public Broadcasting",
  "Public Debt",
  "Public Health",
  "Public Health & Welfare",
  "Public Programs",
  "Public Service Digitization (Digital Citizenship)",
  "Quantum Computing",
  "Quantum Computing",
  "Racial Equity",
  "Ransomware",
  "Rapid Urbanization",
  "Reclamation",
  "Reconstructive Transplantation",
  "Religion & Business",
  "Religion & Government",
  "Religious Studies",
  "Remote Monitoring",
  "Remote Sensing",
  "Rendering Algorithms",
  "Reproductive Health",
  "Research Ethics",
  "Research Methods",
  "Reshoring",
  "Risk Assessment",
  "Robotic Surgery",
  "Robotics",
  "Robotics",
  "Schizophrenia and Bipolar Genetics Research",
  "School Counseling",
  "School Crime",
  "School Safety",
  "School Violence",
  "School-Business Collaboration",
  "Seawater as Renewable Energy",
  "Self-Healing Materials",
  "Sensor Technologies",
  "Sensor Technology",
  "Sensor Technology",
  "Sensor Technology",
  "Small & Medium Enterprises",
  "Smart Cities",
  "Smart Cities",
  "Smart Dust",
  "Smart Dust",
  "Smart Farming",
  "Smart Homes",
  "Smart Spaces",
  "Social Computing",
  "Social Entrepreneurship",
  "Social Justice",
  "Social Media",
  "Social Media & Business",
  "Social Media & Politics",
  "Social Media & Social Networking",
  "Social Media Misinformation",
  "Social Media Privacy",
  "Social Media Security",
  "Social Networking & Public Safety",
  "Social Networking & Public Safety",
  "Social Psychology",
  "Social TV",
  "Social Welfare",
  "Social-Emotional Learning",
  "Socially Conscious Consumers",
  "Socio-Economic Development",
  "Sociology",
  "Software-Defined Networks",
  "Soldier Nanotechnologies",
  "Special Education",
  "Sports Management Studies",
  "Sports Psychology",
  "Startup Business",
  "Stem Cell Treatment/Therapy",
  "Student & Faculty Engagement",
  "Student Creativity",
  "Student Well-Being",
  "Substance Abuse Treatment",
  "Substance Abuse Treatment",
  "Super-Aged Nations",
  "Supply Chain Management",
  "Surveillance & Monitoring",
  "Surveillance Systems",
  "Surveys & Measurement Systems",
  "Sustainability & Sustainable Development",
  "Sustainable Business",
  "Sustainable Infrastructure",
  "Sustainable Infrastructure",
  "Systems & Software Engineering",
  "T Cell Research",
  "Tariffs",
  "Teacher Achievement or Teacher Equity",
  "Teacher-Student Relationships",
  "Teacherpreneuers",
  "Technology Augmentation",
  "Technology Misuse",
  "Technology Regulation",
  "Terrorism",
  "Theoretical Computer Science",
  "Traceability",
  "Training & Retention",
  "Transformed Weapons Procurement",
  "Ubiquitous Computing",
  "Universal Memory",
  "Unmanned Aerial Vehicles",
  "Unmanned Aerial Vehicles",
  "Urban & Regional Development",
  "Urban Agriculture",
  "Urban Poverty",
  "Urban Studies",
  "Vaccines",
  "Veterinary Science",
  "Victimology",
  "Violence in Libraries",
  "Virology",
  "Virtual Incarceration",
  "Voice Technology",
  "Vulnerable Populations",
  "Wartime Media",
  "Waste Management",
  "Water Networks",
  "Water Resource Management",
  "Water Scarcity",
  "Wearable Technology",
  "Wearable Technology",
  "Web Technologies",
  "Welding",
  "Wireless Sensor Networks",
  "Wireless Systems",
  "Women in IT",
  "Women's Rights",
  "Women's Health",
  "World Religions",
  "Youth and Teen Services",
  "Zero-Carbon Natural Gas",
];

export default withStyles(useStyles)(Register);
